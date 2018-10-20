package projet.nadres.nutrition;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    public static final int RESULT_OK = 100;
    private ContentResolver cr;
    private static final String CP_PATH="content://projet.nadres.nutrition.myprovider";
    Spinner sp_quantite;
    TextView nom,calorie,lipide,glucide,proteine,quantite;
    Button button;
    int number;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button = (Button)findViewById(R.id.button);
        nom = (TextView)findViewById(R.id.nom);
        calorie = (TextView)findViewById(R.id.calorie);
        lipide = (TextView)findViewById(R.id.lipide);
        glucide = (TextView)findViewById(R.id.glucide);
        proteine = (TextView)findViewById(R.id.proteine);
        quantite = (TextView)findViewById(R.id.quantite);

        sp_quantite = (Spinner)findViewById(R.id.sp_quantite);
        Intent intent1 = getIntent();
        cr = getContentResolver();
        Cursor c=cr.query(Uri.parse(CP_PATH + "/alimentWithCondition"),
                null,
                "nom = ?",
                new String[]{intent1.getStringExtra("nom")},
                null);
        while(c.moveToNext()){
            nom.setText(c.getString(c.getColumnIndex("nom")));
            calorie.setText(c.getString(c.getColumnIndex("calorie")));
            lipide.setText(c.getString(c.getColumnIndex("lipide")));
            glucide.setText(c.getString(c.getColumnIndex("glucide")));
            proteine.setText(c.getString(c.getColumnIndex("proteine")));
        }
        c.close();

        //le spinner de quantite
        List<String> list = new ArrayList<String>();
        for(int i=0 ;i<10; i++){
            list.add(String.valueOf(i+1));
        }
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        sp_quantite.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent();
                intent2.putExtra("nom", nom.getText().toString());
                intent2.putExtra("calorie", calorie.getText().toString());
                intent2.putExtra("quantite", number);
                InfoActivity.this.setResult(RESULT_OK, intent2);
                InfoActivity.this.finish();
            }
        });

        sp_quantite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                number = Integer.parseInt(adapter.getItem(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                number=1;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }


}
