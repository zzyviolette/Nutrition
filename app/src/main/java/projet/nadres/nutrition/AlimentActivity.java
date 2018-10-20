package projet.nadres.nutrition;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AlimentActivity extends AppCompatActivity {

    private ContentResolver cr;
    private static final String CP_PATH="content://projet.nadres.nutrition.myprovider";
    EditText nom,calorie,lipide,glucide,proteine;
    private ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliment);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nom = (EditText) findViewById(R.id.nom);
        calorie = (EditText) findViewById(R.id.calorie);
        lipide = (EditText) findViewById(R.id.lipide);
        glucide = (EditText) findViewById(R.id.glucide);
        proteine = (EditText) findViewById(R.id.proteine);
        cr=getContentResolver();
    }

    public void Confirmer(View view) {

        if(TextUtils.isEmpty(nom.getText())){
            Toast.makeText(AlimentActivity.this,"Nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(calorie.getText())){
                Toast.makeText(AlimentActivity.this,"Calorie ne peut pas être vide!",Toast.LENGTH_LONG).show();
        }

        if(!TextUtils.isEmpty(nom.getText()) && !TextUtils.isEmpty(calorie.getText())){

            list = new ArrayList<String>();
            Cursor cursor = cr.query(Uri.parse(CP_PATH + "/aliment_table"),
                        null,
                        null,
                        null,
                        null);
            while(cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("nom")));
            }

            if (list.contains(nom.getText().toString())) {

                Toast.makeText(AlimentActivity.this,nom.getText().toString()+" a déjà existe!",Toast.LENGTH_LONG).show();
                }

            else insertAliment();

        }

    }

    public void insertAliment() {
		/*cr.insert(Uri.parse(CP_PATH),
				new ContentValues());*/

        ContentValues values=new ContentValues();
        values.put("nom", nom.getText().toString());
        values.put("calorie", Integer.parseInt(calorie.getText().toString()));
        values.put("lipide",lipide.getText().toString());
        values.put("glucide",glucide.getText().toString());
        values.put("proteine",proteine.getText().toString());

        Uri uri=cr.insert(Uri.parse(CP_PATH+"/aliment_table")
                , values);
        long id= ContentUris.parseId(uri);
//        Toast.makeText(this, "The id of the new insert record:" + id, Toast.LENGTH_LONG).show();
        Toast.makeText(this,nom.getText().toString()+ " a été ajouté!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AlimentActivity.this, MainActivity.class);
        startActivity(intent);
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
