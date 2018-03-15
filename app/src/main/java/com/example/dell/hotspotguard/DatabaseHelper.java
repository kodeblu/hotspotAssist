package com.example.dell.hotspotguard;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by DELL on 3/5/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private final static int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "HotspotAssistant.db";
    private static final  String TABLE = "ssid";
    private static final String COLUMN_SSID_ID = "ssid_id";
    public static final String COLUMN_SSID_USERNAME = "ssid_username";
    private String CREATE_SSID_TABLE ="CREATE TABLE "+TABLE+" (" +
            COLUMN_SSID_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_SSID_USERNAME+" TEXT )";
    private String DROP_SSID_TABLE = "DROP TABLE IF EXISTS "+TABLE;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_SSID_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_SSID_TABLE);
        onCreate(db);
    }

    public Boolean addSsidAcct(String ssid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SSID_USERNAME,ssid);
        long response = db.insert(TABLE,null,values);
        if(response == -1){
            return  false;
        }
        db.close();
        return true;

    }
   public int fetchSsidCount(){
        String[] columns = {
           COLUMN_SSID_USERNAME
       };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_SSID_USERNAME + " != ?";
        String[] selectionArg = { "" } ;
        Cursor cursor = db.query(TABLE,columns,selection,selectionArg,null,null,null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount;
        //return result;
    }

    public Cursor fetchSsid(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TABLE+" WHERE "+COLUMN_SSID_USERNAME+" != ''",null);
        return result;
    }
}
