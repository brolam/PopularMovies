package br.com.brolam.popularmovies.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * A DatabaseHelper é responsável pela criação, manutenção e acesso ao banco de dados
 * SQLite master.db
 * @author Breno Marques
 * @version 1.00
 * @since Release 02
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "master.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        dropTable(sqLiteDatabase, FavoriteModel.Fields.TABLE_NAME);
        sqLiteDatabase.execSQL(FavoriteModel.Fields.getSQLCreate());;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


    }

    private void dropTable(SQLiteDatabase sqLiteDatabase, String tableName){
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s ", tableName ));
    }


}
