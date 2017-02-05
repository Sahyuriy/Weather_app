package com.example.sah.myweather.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBIcon extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Icon_DataBase";
    public static final String TABLE_ICON = "icons";


    public static final String KEY_ID = "_id";
    public static final String KEY_ICON = "icon";



    public DBIcon(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ICON + "(" + KEY_ID + " integer primary key,"
                + KEY_ICON  + " text" +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ICON);

        onCreate(db);
    }
}
