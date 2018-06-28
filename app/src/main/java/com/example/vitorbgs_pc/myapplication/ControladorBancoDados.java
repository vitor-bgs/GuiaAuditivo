package com.example.vitorbgs_pc.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.provider.BlockedNumberContract;
import android.util.Log;

import java.util.List;

public class ControladorBancoDados {

    private BancoDados bancoDados;
    private SQLiteDatabase db;

    public ControladorBancoDados(Context context){
        bancoDados = new BancoDados(context);
        inicializaDB();
    }

    public void apagaDados(){
        db = bancoDados.getWritableDatabase();
        bancoDados.dropDb(db);
    }

    public void inicializaDB(){
        db = bancoDados.getWritableDatabase();
    }

    public void fechaDB(){
        db.close();
    }

    public void insereDados(Ponto ponto){

        long idCoordenada = inserirCoordenadasDB(ponto);

        if(idCoordenada == -1){
            Log.i("", "ControladorBancoDados.insereDados ERRO: idCoordenada = -1");
            return;
        }

        long idFingerprint = inserirFingerprintDB(ponto, idCoordenada);

        if(idFingerprint == -1){
            Log.i("", "ControladorBancoDados.insereDados ERRO: idFingerprint = -1");
            return;
        }

        long idPonto = inserirPontoDB(ponto, idCoordenada);

        if(idPonto == -1){
            Log.i("", "ControladorBancoDados.insereDados ERRO: idPonto = -1");
        }
    }

    private long inserirCoordenadasDB(Ponto ponto){
        ContentValues valores = new ContentValues();
        long resultado;


        //Coordenadas
        valores.put("X", ponto.getCoordenadas().getX());
        valores.put("Y", ponto.getCoordenadas().getY());

        resultado = db.insert(BancoDados.COORDENADAS, null, valores);

        if(resultado == -1){
            Log.i("","Erro ao inserir Coordenadas");
        }

        Log.i("", "Inserir DB: " + resultado);


        return resultado;
    }

    private long inserirFingerprintDB(Ponto ponto, long idCoordenadas){
        long resultado = -1;
        List<ScanResult> fingerprint = ponto.getFingerprint();


        for(int i = 0; i < fingerprint.size(); i++){

            ContentValues valores = new ContentValues();

            valores.put("BSSID", fingerprint.get(i).BSSID);
            valores.put("INTENSIDADE", fingerprint.get(i).level);
            valores.put("ID", idCoordenadas);

            resultado = db.insert(BancoDados.FINGERPRINT, null, valores);

            if(resultado == -1){
                Log.i("","Erro ao inserir Coordenadas");
            }
        }


        return resultado;
    }

    private long inserirPontoDB(Ponto ponto, long idCoordenadas){
        ContentValues valores = new ContentValues();
        long resultado;


        valores.put("IDCOORDENADAS", idCoordenadas);
        valores.put("IDFINGERPRINT", idCoordenadas);
        valores.put("NOME", ponto.getNome());

        resultado = db.insert(BancoDados.PONTOS, null, valores);

        if(resultado == -1){
            Log.i("","Erro ao inserir Coordenadas");
        }



        return resultado;
    }

    public Cursor consultarCoordenadas(){
        Cursor cursor;
        String[] campos = {BancoDados.ID, "X", "Y"};
        cursor = db.query(BancoDados.COORDENADAS, campos, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }


        return cursor;
    }

    public Cursor verificarFingerprint(String BSSID, String Level){
        Cursor cursor = null;

        String[] campos = {"BSSID", "INTENSIDADE", "ID"};

        String whereClause = "BSSID = ? AND INTENSIDADE = ?";

        String[] whereArgs = {BSSID, Level};

        cursor = db.query(BancoDados.FINGERPRINT, campos, whereClause, whereArgs, null, null, null, "5");

        try{
            if(cursor != null){
                cursor.moveToFirst();

                Log.i("", cursor.getString(cursor.getColumnIndex("BSSID")));
                Log.i("", cursor.getString(cursor.getColumnIndex("INTENSIDADE")));
                Log.i("", cursor.getString(cursor.getColumnIndex("ID")));
            }

        }
        catch (Exception e){

        }



        return cursor;
    }


    public Cursor consultarPonto(int id){
        Cursor cursor = null;

        String[] campos = {BancoDados.ID, "NOME", "IDCOORDENADAS"};
        String whereClause = BancoDados.ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        cursor = db.query(BancoDados.PONTOS, campos, whereClause, whereArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }


        return cursor;
    }

    public Cursor consultarCoordenada(int id){
        Cursor cursor = null;

        String[] campos = {BancoDados.ID, "X", "Y"};
        String whereClause = BancoDados.ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        cursor = db.query(BancoDados.COORDENADAS, campos, whereClause, whereArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }


        return cursor;
    }

    public Cursor verificarDB(){
        String[] campos = {"_id", "BSSID", "INTENSIDADE"};
        return db.query("FINGERPRINT", campos, null, null, null,  null, null);
    }

}
