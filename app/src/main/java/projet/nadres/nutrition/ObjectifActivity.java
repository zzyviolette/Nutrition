package projet.nadres.nutrition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ObjectifActivity extends AppCompatActivity {

    EditText min,max;
    TextView actuel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objectif);
        min = (EditText)findViewById(R.id.min);
        max = (EditText)findViewById(R.id.max);
        actuel = (TextView)findViewById(R.id.txt_actuel);
        SharedPreferences objectif = getSharedPreferences("objectif", MODE_PRIVATE);
        int min_cal = objectif.getInt("min", 0);
        int max_cal = objectif.getInt("max", 0);
        actuel.setText("Min actuel: "+min_cal+"  "+"Max actuel: "+max_cal);

    }

    public void Confirmer(View view) {

        int min_cal = Integer.parseInt(min.getText().toString());
        int max_cal = Integer.parseInt(max.getText().toString());

        if(max_cal < min_cal){
            Toast.makeText(ObjectifActivity.this,"Max Calories doit être supérieur à Min Calories!", Toast.LENGTH_LONG).show();
        }else{

            SharedPreferences objectif = getSharedPreferences("objectif", MODE_PRIVATE);
            SharedPreferences.Editor editor = objectif.edit();
            editor.putInt("min",min_cal);
            editor.putInt("max", max_cal);
            editor.commit();
            Toast.makeText(ObjectifActivity.this, "L'objectif a été sauvegardé!", Toast.LENGTH_LONG).show();
            Intent  intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }
    }
}
