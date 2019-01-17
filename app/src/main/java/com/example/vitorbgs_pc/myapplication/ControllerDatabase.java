package com.example.vitorbgs_pc.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.List;

public class ControllerDatabase {

    private Database database;
    private SQLiteDatabase db;

    public ControllerDatabase(Context context){
        database = new Database(context);
        initializeDB();
    }

    public void initializeDB(){
        db = database.getWritableDatabase();
    }

    public void insertData(MapPoint mapPoint){

        int lastId = getLastId();

        long fingerprintId = insertFingerprintDB(mapPoint, lastId + 1);

        if(fingerprintId == -1){
            Log.i("", "ControllerDatabase.insertData ERRO: fingerprintId = -1");
            return;
        }

        long pointID = insertPointDB(mapPoint, lastId + 1);

        if(pointID == -1){
            Log.i("", "ControllerDatabase.insertData ERRO: pointID = -1");
        }
    }

    private int getLastId(){
        try{
            String[] campos = {"_id"};
            Cursor cursor = db.rawQuery("SELECT _id FROM PONTOS ORDER BY _id DESC LIMIT 1", null);
            cursor.moveToFirst();
            return Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));

        }catch(Exception e){
            Log.i("", "Erro verificar ultimo ID");
        }
        return 0;
    }

    private long insertFingerprintDB(MapPoint mapPoint, long id){
        long resultado = -1;
        List<ScanResult> fingerprint = mapPoint.getFingerprint();


        for(int i = 0; i < fingerprint.size(); i++){

            ContentValues valores = new ContentValues();

            valores.put("BSSID", fingerprint.get(i).BSSID);
            valores.put("INTENSIDADE", fingerprint.get(i).level);
            valores.put("ID", id);

            resultado = db.insert(Database.FINGERPRINT, null, valores);

            if(resultado == -1){
                Log.i("","Erro ao inserir Fingerprint");
            }
        }


        return resultado;
    }

    private long insertPointDB(MapPoint mapPoint, long id){
        ContentValues valores = new ContentValues();
        long resultado;


        valores.put("X", mapPoint.getX());
        valores.put("Y", mapPoint.getY());
        valores.put("IDFINGERPRINT", id);
        valores.put("NOME", mapPoint.getName());

        resultado = db.insert(Database.PONTOS, null, valores);

        if(resultado == -1){
            Log.i("","Erro ao inserir Pontos");
        }



        return resultado;
    }

    public Cursor consultarCoordenadas(){
        Cursor cursor;
        String[] campos = {Database.ID, "X", "Y"};
        cursor = db.query(Database.PONTOS, campos, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }


        return cursor;
    }


    public Cursor consultarPonto(int id){
        Cursor cursor = null;

        String[] campos = {Database.ID, "NOME", "X", "Y"};
        String whereClause = Database.ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        cursor = db.query(Database.PONTOS, campos, whereClause, whereArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor consultarFingerprint(List<ScanResult> scanResults){
        String[] campos = {"BSSID", "INTENSIDADE", "ID"};
        Cursor cursor;

        String whereClause = "(BSSID = ? AND (INTENSIDADE > ? AND INTENSIDADE < ?))";

        String[] whereArgs = new String[scanResults.size()*3];

        for(int i = 0; i < scanResults.size(); i++){
            whereArgs[i*3] = scanResults.get(i).BSSID;
            whereArgs[i*3 + 1] = Integer.toString(scanResults.get(i).level - 2);
            whereArgs[i*3 + 2] = Integer.toString(scanResults.get(i).level + 2);

            if(i < scanResults.size() -1){
                whereClause += " OR (BSSID = ? AND (INTENSIDADE > ? AND INTENSIDADE < ?))";
            }
        }

        cursor = db.query(Database.FINGERPRINT, campos, whereClause, whereArgs, null, null, null);

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
