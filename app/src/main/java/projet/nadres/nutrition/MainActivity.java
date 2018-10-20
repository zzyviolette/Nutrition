package projet.nadres.nutrition;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private static final String TAG = "info";

    private ContentResolver cr;
    Cursor c;
    private static final String CP_PATH="content://projet.nadres.nutrition.myprovider";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cr=getContentResolver();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.aliment:
                        intent = new Intent(MainActivity.this, AlimentActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.repas:
                        intent = new Intent(MainActivity.this, RepasActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.objectif:
                        intent = new Intent(MainActivity.this, ObjectifActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.statistique:
                        intent = new Intent(MainActivity.this, StatistiqueActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        if(item.getItemId()==R.id.download){
            ExportToCSV(touslesAliments(),"Aliments");
            Toast.makeText(MainActivity.this,"Les aliments ont été téléchargés, merci de vérifier dans le /sdcard/exportData!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }




    public void ExportToCSV(List<String> record, String fileName) {

        //新建一个文件对象
        File file = new File(Environment.getExternalStorageDirectory() + "/exportData/" + fileName + ".csv");
//        File file = new File("/sdcard/exportData" + fileName + ".csv");
        //判断文件是否存在
        if (!file.exists()) {
            //不存在则创建多级目录
            boolean mkdir = file.getParentFile().mkdirs();
            Log.d(TAG, "CSVUtil exportCSV mkdir: " + mkdir);
        } else {
            //存在则删除旧文件
            boolean delete = file.delete();
            Log.d(TAG, "CSVUtil exportCSV delete: " + delete);
        }
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            //第一行
            bufferedWriter.append("Nom,Calorie,Lipide,Glucide,Proteine");
            bufferedWriter.newLine();
            for (String r : record) {
                bufferedWriter.append(r);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public List<String> touslesAliments() {

        c=cr.query(Uri.parse(CP_PATH + "/aliment_table"),
                null,
                null,
                null,
                null);
        List<String> aliments= new ArrayList<>();
        while(c.moveToNext()){
            aliments.add(c.getString(1) + ',' + c.getString(2)+','+c.getString(3)+','+c.getString(4)+','+c.getString(5));
        }
//        Collections.sort(aliments);
        c.close();
        return aliments;

    }
}