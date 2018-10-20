package projet.nadres.nutrition;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StatistiqueActivity extends AppCompatActivity {

    private ContentResolver cr;
    private static final String CP_PATH="content://projet.nadres.nutrition.myprovider";
    private CombinedChart combinedChart;
    List<Integer> sommes;
    List<String> jours;
    private String dernierJour;
    private TextView repas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);
        repas = (TextView)findViewById(R.id.dernierRepas);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cr=getContentResolver();
        //obtenir tous les jours et inverse pour commencer par les derniers jours
        jours = new ArrayList<>();
        for(int i=0,j=tousLesjours().size()-1; i < tousLesjours().size(); i++,j--){
            jours.add(i,tousLesjours().get(j));
        }
        //obtenir la sommme de calorie pour chaque jour
        sommes = new ArrayList<>();
        for(int i=0; i<jours.size(); i++) {
            sommes.add(sommeDeJour(String.valueOf(jours.get(i))));
        }

        //le nombre de colone
        int colone = 7;
        if(jours.size()<7){
            colone  = jours.size();
        }

        //les attributs de combinedChart
        combinedChart = (CombinedChart) findViewById(R.id.chart);
        combinedChart.setDrawBorders(true); // 显示边界
        combinedChart.getDescription().setEnabled(false);  // 不显示备注信息
        combinedChart.setPinchZoom(true); // 比例缩放
        combinedChart.animateY(1500);

        if(jours.size()>0){


        //les attribut de XLabels
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false);
        /*解决左右两端柱形图只显示一半的情况 只有使用CombinedChart时会出现，如果单独使用BarChart不会有这个问题*/
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(colone - 0.5f);

        xAxis.setLabelCount(colone); // 设置X轴标签数量
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴标签位置，BOTTOM在底部显示，TOP在顶部显示

        //les derniers jours -> x labels
        final String[] x_labelValues =  new String[colone];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(int i =0; i<colone;i++){
            Date date = new Date();
            try {
                date = sdf.parse(jours.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            x_labelValues[colone - 1 - i] = sdf.format(date);
        }

            dernierJour = x_labelValues[x_labelValues.length-1];
            leDernierRepas(dernierJour);
//            repas.setText(leDernierRepas(dernierJour));

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return x_labelValues[(int) value];
            }

            //我们不画数字，所以不需要小数位数
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        xAxis.setGranularity(1f);  //最小轴步骤（间隔）为1
        xAxis.setLabelRotationAngle(-20);
        xAxis.setValueFormatter(formatter);

        //Yleft
        YAxis axisLeft = combinedChart.getAxisLeft(); // 获取左边Y轴操作类
        axisLeft.setAxisMinimum(0); // 设置最小值
        axisLeft.setGranularity(10); // 设置Label间隔
        axisLeft.setLabelCount(10);// 设置标签数量

        //Yright
        YAxis axisRight = combinedChart.getAxisRight(); // 获取右边Y轴操作类
        axisRight.setDrawGridLines(false); // 不绘制背景线，上面左边Y轴并没有设置此属性，因为不设置默认是显示的
        axisRight.setGranularity(10); // 设置Label间隔
        axisRight.setAxisMinimum(0); // 设置最小值
        axisRight.setLabelCount(20); // 设置标签个数

        //bardata
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < colone; i++) {
            barEntries.add(new BarEntry(colone - 1 - i, Float.parseFloat(String.valueOf(sommes.get(i)))));
        }

        //les attributs de bardataset
        BarDataSet barDataSet = new BarDataSet(barEntries, "Calorie");  // 新建一组柱形图，"LAR"为本组柱形图的Label
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS); // 设置柱形图颜色
        barDataSet.setValueTextColor(Color.parseColor("#0288d1")); //  设置柱形图顶部文本颜色
        barDataSet.setValueTextSize(13);
        barDataSet.setValueTextColor(Color.BLACK);
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);// 添加一组柱形图，如果有多组柱形图数据，则可以多次addDataSet来设置

        //obtenir le min et max de l'objectif pour afficher
        SharedPreferences objectif = getSharedPreferences("objectif", MODE_PRIVATE);
        int min = objectif.getInt("min", 0);
        int max = objectif.getInt("max", 0);

        //LineChart de min
        List<Entry> minEntries = new ArrayList<>();
        for (int i = 0; i < colone; i++) {
            minEntries.add(new Entry(i, min));
        }
        LineDataSet minDataSet = new LineDataSet(minEntries, "MIN Calorie");
        minDataSet.setColor(Color.RED);
        minDataSet.setValueTextColor(Color.GRAY);
        minDataSet.setLineWidth(3f);
        minDataSet.setDrawCircles(false);
        minDataSet.setValueTextSize(10);
        minDataSet.setHighlightEnabled(false);

        //LineChart de max
        List<Entry> maxEntries = new ArrayList<>();
        for (int i = 0; i < colone; i++) {
            maxEntries.add(new Entry(i, max));
        }


        LineDataSet maxDataSet = new LineDataSet(maxEntries, "MAX Calorie");
        maxDataSet.setColor(Color.BLUE);
        maxDataSet.setValueTextColor(Color.GRAY);
        maxDataSet.setLineWidth(3f);
        maxDataSet.setDrawCircles(false);
        maxDataSet.setValueTextSize(10);
        maxDataSet.setHighlightEnabled(false);

        LineData lineData = new LineData();
        lineData.addDataSet(minDataSet);
        lineData.addDataSet(maxDataSet);


        //set tous les donnees pour le combinedChart

        CombinedData combinedData = new CombinedData(); // 创建组合图的数据源
        combinedData.setData(barData);  // 添加柱形图数据源
        if(min==0&&max==0){

        }else{
            combinedData.setData(lineData);       // 添加折线图数据源
        }

        combinedChart.setData(combinedData); // 为组合图设置数据源
        }

    }

    public int sommeDeJour(String jour) {
        Cursor c=cr.query(Uri.parse(CP_PATH + "/repasWithCondition"),
                null,
                "jour = ?",
                new String[]{jour},
                null);
        int somme = 0;
        while(c.moveToNext()){
            somme += c.getInt(c.getColumnIndex("somme"));
        }
        c.close();
        return somme;
    }

    public void leDernierRepas(String dernierJour) {
        String calorie=null;
        Cursor c=cr.query(Uri.parse(CP_PATH + "/repasWithCondition"),
                null,
                "jour = ?",
                new String[]{dernierJour},
                null);
        if(c.moveToLast()){
            calorie = c.getString(c.getColumnIndex("somme"));
        }
        if(calorie==null){
            repas.setText("");
        }
        repas.setText("La somme calorique du dernier repas est " +calorie +"Kcal");
    }

    public List<String> tousLesjours() {
        Cursor c=cr.query(Uri.parse(CP_PATH + "/tousLesJours"),
                null,
                null,
                null,
                null);
        List<String> jours= new ArrayList<>();
        while(c.moveToNext()){
            jours.add(c.getString(c.getColumnIndex("jour")));
        }
        Collections.sort(jours);
        c.close();
        return jours;

    }
}