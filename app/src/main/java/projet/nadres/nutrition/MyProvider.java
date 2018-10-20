package projet.nadres.nutrition;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by zhouziyi on 2017/11/22.
 */
public class MyProvider extends ContentProvider {

    private static final String LOG = "MyProvider";

    public static final String AUTOHORITY = "projet.nadres.nutrition.myprovider";
    private static final int  ALIMENT= 1;
    private static final int  REPAS = 2;
    private static final int ALIMENT_CONDITION = 3;
    private static final int REPAS_CONDITION = 4;
    private static final int TOUS_LES_JOURS =5;

    DBLiteHelper helper ;

    private static final UriMatcher matcher;
    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTOHORITY,"aliment_table", ALIMENT);
        matcher.addURI(AUTOHORITY,"repas_table", REPAS);
        matcher.addURI(AUTOHORITY,"alimentWithCondition", ALIMENT_CONDITION);
        matcher.addURI(AUTOHORITY,"repasWithCondition", REPAS_CONDITION);
        matcher.addURI(AUTOHORITY,"tousLesJours", TOUS_LES_JOURS);
    }

    public MyProvider() {
    }

    @Override
    public String getType(Uri uri) {
//        switch (mMatcher.match(uri)) {
//            case Constant.ITEM:
//                return Constant.CONTENT_TYPE;
//            case Constant.ITEM_ID:
//                return Constant.CONTENT_ITEM_TYPE;
//            default:
//                throw new IllegalArgumentException("Unknown URI"+uri);
//        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        Log.d(LOG, "Uri=" + uri.toString());
        long id = 0;
        String path;
        switch (code) {
            case ALIMENT:
                id = db.insert("aliment_table", null, values);
                path = "aliment_table";
                break;
            case REPAS:
                id = db.insert("repas_table", null, values);
                path = "repas_table";
                break;
            default:
                throw new UnsupportedOperationException("this insert not yet implemented");
        }
        Uri.Builder builder = (new Uri.Builder())
                .authority(AUTOHORITY)
                .appendPath(path);

        return ContentUris.appendId(builder, id).build();

    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        helper = new DBLiteHelper(getContext());

        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = helper.getReadableDatabase();
        int code = matcher.match(uri);
        Cursor cursor;
        switch (code) {
            case ALIMENT:

//                cursor = db.query("aliment_table", projection, selection,
//                        selectionArgs, null, null, sortOrder);
                cursor = db.rawQuery("select * from aliment_table", null);

                break;
            case REPAS:
                cursor = db.query("repas_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case ALIMENT_CONDITION:
                String aliment_sql = "select * from aliment_table";
                if (selection != null) {
                    aliment_sql += " where " + selection;
                }
                cursor = db.rawQuery(aliment_sql, selectionArgs);

                break;
            case REPAS_CONDITION:
                String repas_sql = "select * from repas_table";
                if (selection != null) {
                    repas_sql += " where " + selection;
                }
                cursor = db.rawQuery(repas_sql, selectionArgs);

                break;
            case TOUS_LES_JOURS:
                String jours_sql = "select distinct jour from repas_table";
                cursor = db.rawQuery(jours_sql, null);

                break;
            default:
                Log.d("Uri provider =", uri.toString());
                throw new UnsupportedOperationException("this query is not yet implemented  " +
                        uri.toString());
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
