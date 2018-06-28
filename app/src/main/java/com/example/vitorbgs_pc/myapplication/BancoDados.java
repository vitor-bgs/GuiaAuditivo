package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "guiaauditivomapa.db";
    public static final String COORDENADAS = "COORDENADAS";
    public static final String PONTOS = "PONTOS";
    public static final String FINGERPRINT = "FINGERPRINT";
    public static final String ID = "_id";
    public static final int VERSAO = 1;

    public BancoDados(Context context){
        super(context, NOME_BANCO, null, VERSAO);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + COORDENADAS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "X INTEGER, " +
                "Y INTEGER " +
                ");");

        db.execSQL("CREATE TABLE " + PONTOS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "IDCOORDENADAS INTEGER," +
                "IDFINGERPRINT INTEGER," +
                "NOME TEXT);");

        db.execSQL("CREATE TABLE " + FINGERPRINT + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BSSID TEXT," +
                "INTENSIDADE INTEGER," +
                "ID INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS COORDENADAS");
        db.execSQL("DROP TABLE IF EXISTS PONTOS");
        db.execSQL("DROP TABLE IF EXISTS FINGERPRINT");
        onCreate(db);
    }

    public void dropDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS COORDENADAS");
    }

}
