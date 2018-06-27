package com.example.vitorbgs_pc.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ControladorBancoDados {

    private BancoDados bancoDados;
    private SQLiteDatabase db;

    public ControladorBancoDados(Context context){
        bancoDados = new BancoDados(context);
    }

    public void apagaDados(){
        db = bancoDados.getWritableDatabase();
        bancoDados.dropDb(db);
    }

    public void inicializaDB(){
        db = bancoDados.getWritableDatabase();
        bancoDados.onCreate(db);
    }

    public void insereDados(Ponto ponto){
        ContentValues valores = new ContentValues();
        long resultado;

        db = bancoDados.getWritableDatabase();

        //Coordenadas
        valores.put("X", ponto.getCoordenadas().getX());
        valores.put("Y", ponto.getCoordenadas().getY());

        resultado = db.insert("COORDENADAS", null, valores);

        Log.i("", "Inserir DB: " + resultado);
    }

    public Cursor carregaDados(){
        Cursor cursor;
        String[] campos = {bancoDados.ID, "X", "Y"};
        db = bancoDados.getReadableDatabase();
        cursor = db.query(bancoDados.COORDENADAS, campos, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }


}
