package projet.nadres.nutrition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by zhouziyi on 2017/11/22.
 */



public class DBLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "info";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Nutrition.db";
    private final Context myContext;

    /* les attributs */
    //Aliment
    public static final String TABLE_ALIMENT = "aliment_table";
    public static final String _ID= "_id";
    public static final String NOM= "nom";
    public static final String CALORIE = "calorie";
    public static final String LIPIDE = "lipide";
    public static final String GLUCIDE = "glucide";
    public static final String PROTEINE = "proteine";

    //Repas
    public static final String TABLE_REPAS = "repas_table";
    public static final String JOUR = "jour";
    public static final String SOMME = "somme";


    private static final String SQL_CREATE_ALIMENT =
            "CREATE TABLE " + TABLE_ALIMENT + " (" +
                    _ID + " INTEGER AUTO_INCREMENT," +
                    NOM + " VARCHAR(255) NOT NULL PRIMARY KEY, " +
                    CALORIE + " VARCHAR(255) NOT NULL, " +
                    LIPIDE + " VARCHAR(255) DEFAULT '', " +
                    GLUCIDE + " VARCHAR(255) DEFAULT '', " +
                    PROTEINE + " VARCHAR(255) DEFAULT '' "+
                    " )";

    private static final String SQL_CREATE_REPAS =
            "CREATE TABLE " + TABLE_REPAS + " (" +
                    _ID + " INTEGER AUTO_INCREMENT," +
                    JOUR + " VARCHAR(255), " +
                    SOMME + " VARCHAR(255)" +
                    " )";

    public DBLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ALIMENT);
        db.execSQL(SQL_CREATE_REPAS);
        //insert quelques données

        importCSV(db);

        db.execSQL("insert into repas_table(jour,somme) values('2017-01-02','435')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-02','600')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-03','456')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-03','1367')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-03-04','1532')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-03-20','2237')");
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-06','2753')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void importCSV(SQLiteDatabase db) {


        InputStream is = myContext.getResources().openRawResource(R.raw.data);
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int count = -1;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(isr);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] datas = line.split(",");
                //第一次存入的是列名而不是数据，需要排除
                System.out.println(datas.length);
                if (count >= 0) {
                    //重新存入数据库
                    db.execSQL("insert into aliment_table(nom,calorie,lipide,glucide,proteine) values(?,?,?,?,?)",new Object[]{datas[0],datas[1],datas[2],datas[3],datas[4]});

                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
