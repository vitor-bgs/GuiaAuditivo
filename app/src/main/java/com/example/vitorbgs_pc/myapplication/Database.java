package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASENAME = "guiaauditivomap.db";

    public static final String POINTS = "POINTS";
    public static final String NAME = "NAME";
    public static final String X = "X";
    public static final String Y = "Y";



    public static final String FINGERPRINT = "FINGERPRINT";
    public static final String IDFINGERPRINT = "IDFINGERPRINT";
    public static final String BSSID = "BSSID";
    public static final String INTENSITY = "INTENSITY";


    public static final String ID = "_id";
    public static final int VERSION = 1;

    public Database(Context context){
        super(context, DATABASENAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + POINTS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "X INTEGER," +
                "Y INTEGER," +
                "IDFINGERPRINT INTEGER," +
                "NAME TEXT);");

        db.execSQL("CREATE TABLE " + FINGERPRINT + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BSSID TEXT," +
                "INTENSITY INTEGER," +
                "IDFINGERPRINT INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS PONTOS");
        db.execSQL("DROP TABLE IF EXISTS FINGERPRINT");
        onCreate(db);
    }

}