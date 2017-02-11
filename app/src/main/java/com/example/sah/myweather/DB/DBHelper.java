package com.example.sah.myweather.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "My_DataBase";
    public static final String TABLE_WEATHER = "weather";


    public static final String KEY_ID = "_id";



    public static final String KEY_TIME = "date";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_DESCR = "description";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_PRESSURE = "pressure";
    public static final String KEY_WIND_SPEED = "wind_speed";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_WEATHER + "(" + KEY_ID + " integer primary key,"
                + KEY_TIME + " text," +  KEY_DESCR + " text," + KEY_TEMPERATURE + " text,"
                + KEY_HUMIDITY + " text," + KEY_PRESSURE + " text,"+ KEY_WIND_SPEED + " text" +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WEATHER);

        onCreate(db);
    }

//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        onUpgrade(db, oldVersion, newVersion);
//    }

}
