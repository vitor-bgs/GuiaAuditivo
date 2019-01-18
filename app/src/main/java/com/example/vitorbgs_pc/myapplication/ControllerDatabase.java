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
            String[] campos = { Database.ID };
            Cursor cursor = db.query(Database.POINTS, campos, null, null, null, null, null);
            cursor.moveToLast();
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            return id;

        }catch(Exception e){
            Log.i("", "Erro verificar ultimo ID");
        }
        return 0;
    }

    private long insertFingerprintDB(MapPoint mapPoint, long id){
        long result = -1;
        List<ScanResult> fingerprint = mapPoint.getFingerprint();


        for(int i = 0; i < fingerprint.size(); i++){

            ContentValues values = new ContentValues();

            values.put(Database.BSSID, fingerprint.get(i).BSSID);
            values.put(Database.INTENSITY, fingerprint.get(i).level);
            values.put(Database.IDFINGERPRINT, id);

            result = db.insert(Database.FINGERPRINT, null, values);

            if(result == -1){
                Log.i("","Erro ao inserir Fingerprint");
            }
        }


        return result;
    }

    private long insertPointDB(MapPoint mapPoint, long id){
        ContentValues values = new ContentValues();
        long result;


        values.put(Database.X, mapPoint.getX());
        values.put(Database.Y, mapPoint.getY());
        values.put(Database.IDFINGERPRINT, id);
        values.put(Database.NAME, mapPoint.getName());

        result = db.insert(Database.POINTS, null, values);

        if(result == -1){
            Log.i("","Erro ao inserir Pontos");
        }

        return result;
    }

    public Cursor getPoint(int id){
        Cursor cursor = null;

        String[] campos = {Database.ID, Database.NAME, Database.X, Database.Y};
        String whereClause = Database.ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        cursor = db.query(Database.POINTS, campos, whereClause, whereArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor getAllPoints(){
        String[] fields = { Database.ID, Database.NAME, Database.X, Database.Y, Database.IDFINGERPRINT};
        return db.query(Database.POINTS, fields, null, null, null, null, null);
    }

    public Cursor getAllFingerprints(){
        String[] fields = {Database.ID, Database.IDFINGERPRINT, Database.BSSID, Database.INTENSITY};
        return db.query(Database.FINGERPRINT, fields, null, null, null, null, null);
    }
}
