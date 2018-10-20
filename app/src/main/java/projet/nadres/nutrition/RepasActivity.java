package projet.nadres.nutrition;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RepasActivity extends AppCompatActivity{
    public static final int RESULT_OK = 100;
    private static final String CP_PATH="content://projet.nadres.nutrition.myprovider";
    private ContentResolver cr;
    private TextView txt_calorie,txt_date,txt_list;
    private Spinner spinner;
    private ArrayList<String> sp_list;
    private ArrayAdapter<String> sp_adapter;
    private ListView lv;
    private List<Map<String, Object>> lv_list;
    private SimpleAdapter lv_adapter;
    private  int calorie_total;
    private Calendar  calendar;
    private Button btn_confirmer;
    private Comparator<String> comparator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repas);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comparator = new Comparator<String>() {
            //默认o1大于o2
            public int compare(String o1, String o2) {

                if(o1.equals("Ajouter un aliment dans un repas")){
                    return -1;//negative o1->o2
                }
                if(o2.equals("Ajouter un aliment dans un repas")){
                    return 1;//positive o2->o1
                }
                return o1.compareTo(o2); //normale

            }
        };

        //spinner
        spinner = (Spinner) findViewById(R.id.spinner);


        cr=getContentResolver();

        //remplir le spinner
        sp_list = new ArrayList<String>();
        sp_list.add(0, "Ajouter un aliment dans un repas");
        Cursor cursor = cr.query(Uri.parse(CP_PATH + "/aliment_table"),
                null,
                null,
                null,
                null);
        while(cursor.moveToNext()){
            sp_list.add(cursor.getString(cursor.getColumnIndex("nom")));
        }
        cursor.close();
        Collections.sort(sp_list,comparator);
//        sp_list = Sort(sp_list);

        txt_calorie = (TextView) findViewById(R.id.txt_calorie);
        txt_list = (TextView)findViewById(R.id.txt_list);

        //enregistrer les aliments dans un repas
        btn_confirmer = (Button)findViewById(R.id.btn_confirmer);

        //listview pour les aliments choisis
        lv = (ListView) findViewById(R.id.lv);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv_list = new ArrayList<Map<String,Object>>();
        lv_adapter = new SimpleAdapter(this, lv_list,
                R.layout.list_item, new String[] { "first", "second", "third" },
                new int[] { R.id.first, R.id.second, R.id.third });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                if(pos>0){

                    String nom = String.valueOf(lv_list.get(pos).get("first"));
                    String calorie = String.valueOf(lv_list.get(pos).get("second"));
                    String quantite = String.valueOf(lv_list.get(pos).get("third"));
                    calorie_total -= Integer.parseInt(calorie)*Integer.parseInt(quantite);
                    txt_calorie.setText("Somme de calorie : " + calorie_total);
                    ajouterAlimentSpinner(nom);

                    lv_list.remove(pos);
                    lv.setAdapter(lv_adapter);

                }

                return true;
            }
        });
        //choisir le date
        txt_date=(TextView)findViewById(R.id.txt_date);

        //transfert le type de donnee de jour à string pour le stocker dans la base de donnees
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //aujourd'hui
        calendar = Calendar.getInstance(Locale.FRANCE);
        String str = df.format(calendar.getTime());
        txt_date.setText(str);


    }

    @Override
    protected void onStart(){
        super.onStart();
        sp_adapter = new ArrayAdapter<String>(this,R.layout.spinner_list_item, sp_list);
        //style de spinner
        sp_adapter.setDropDownViewResource(R.layout.dropdown_list_item);
        spinner.setAdapter(sp_adapter);

        //set listener de spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    Intent intent = new Intent();
                    intent.putExtra("nom", sp_adapter.getItem(position).toString());
                    intent.setClass(RepasActivity.this, InfoActivity.class);
                    // pour obtenir les données de infoActivity
                    startActivityForResult(intent, 1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lv.setAdapter(lv_adapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String nom = data.getStringExtra("nom");
                String calories = data.getStringExtra("calorie");
                int quantite = data.getIntExtra("quantite", 1);

                supprimerAlimentSpinner(nom);

                if(lv_list.size()==0){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("first", "Nom");
                    map.put("second", "Calorie");
                    map.put("third", "Quantite");
                    lv_list.add(0, map);
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("first", nom);
                map.put("second", calories);
                map.put("third", quantite);
                lv_list.add(map);
                if(lv_list.size()==0){
                    calorie_total = 0;
                }
                else{
                    calorie_total+= (int)Float.parseFloat(calories.toString())*quantite;
                }

                break;

            default:
                break;
        }

        if(lv_list.size()!=0){
            txt_calorie.setText("Somme de calorie : " + calorie_total);
            txt_list.setText("Les aliments choisis : (Long Click pour supprimer) ");
            btn_confirmer.setVisibility(View.VISIBLE);
        }
        lv_adapter.notifyDataSetChanged();
    }

    public void date(View view) {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String y,m,d;
                y = String.valueOf(year);
                //de 1 -> 01
                if(month<9){
                    m = "0"+(month+1);
                }else{
                    m = String.valueOf(month+1);
                }

                if(dayOfMonth<10){
                    d = "0"+dayOfMonth;
                }else {
                    d = String.valueOf(dayOfMonth);
                }

                txt_date.setText(y+"-"+m+"-"+d);
            }
        };

        Calendar mCalendar=Calendar.getInstance(Locale.FRANCE);
        int year=mCalendar.get(Calendar.YEAR);
        int month=mCalendar.get(Calendar.MONTH);
        int day=mCalendar.get(Calendar.DAY_OF_MONTH);
        // 设置初始日期
        DatePickerDialog dialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
                dateSetListener, year, month, day);
        dialog.show();


    }

    public void Confirmer(View view) {

        String jour = txt_date.getText().toString();
        //2017-12-02

        if(calorie_total==0){
            Toast.makeText(RepasActivity.this,"Il n'y pas de aliments dans ce repas!",Toast.LENGTH_LONG).show();
        }
        else {
            ContentValues values=new ContentValues();
            values.put("jour", jour);
            values.put("somme", calorie_total);

            Uri uri = cr.insert(Uri.parse(CP_PATH + "/repas_table")
                    , values);
            long id= ContentUris.parseId(uri);
            Toast.makeText(this, "Un nouveau repas a été ajouté! " , Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }

        Intent intent = new Intent(RepasActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void ajouterAlimentSpinner(String nom){

        sp_list.add(nom);
        Collections.sort(sp_list,comparator);
        sp_adapter.notifyDataSetChanged();
    }

    public void supprimerAlimentSpinner(String nom) {

        sp_adapter.notifyDataSetChanged();

        for (int i = 1; i < sp_list.size(); i++) {
            if (sp_list.get(i).equals(nom)) {
                sp_list.remove(i);
                Collections.sort(sp_list,comparator);
//                Toast.makeText(RepasActivity.this, "index" + i, Toast.LENGTH_LONG).show();
                sp_adapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            txt_date.setTextSize(25);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) txt_date.getLayoutParams();
            lp.topMargin=10;
            lp = (LinearLayout.LayoutParams) spinner.getLayoutParams();
            lp.topMargin=10;
            lp = (LinearLayout.LayoutParams) txt_list.getLayoutParams();
            lp.topMargin=10;


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
            txt_date.setTextSize(35);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) txt_date.getLayoutParams();
            lp.topMargin=20;
            lp = (LinearLayout.LayoutParams) spinner.getLayoutParams();
            lp.topMargin=30;
            lp = (LinearLayout.LayoutParams) txt_list.getLayoutParams();
            lp.topMargin=20;
        }
    }

//    protected void onSaveInstanceState(Bundle outState) {
//
//        super.onSaveInstanceState(outState);
//
//    }
//
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//
//        super.onRestoreInstanceState(savedInstanceState);
//    }




}




