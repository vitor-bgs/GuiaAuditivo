package com.example.vitorbgs_pc.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.provider.BlockedNumberContract;
import android.util.Log;

import java.util.List;
import java.util.logging.Level;

public class ControladorBancoDados {

    private BancoDados bancoDados;
    private SQLiteDatabase db;

    public ControladorBancoDados(Context context){
        bancoDados = new BancoDados(context);
        inicializaDB();
    }

    public void inicializaDB(){
        db = bancoDados.getWritableDatabase();
    }

    public void fechaDB(){
        db.close();
    }

    public void insereDados(Ponto ponto){

        int ultimoid = verificarUltimoID();

        long idFingerprint = inserirFingerprintDB(ponto, ultimoid + 1);

        if(idFingerprint == -1){
            Log.i("", "ControladorBancoDados.insereDados ERRO: idFingerprint = -1");
            return;
        }

        long idPonto = inserirPontoDB(ponto, ultimoid + 1);

        if(idPonto == -1){
            Log.i("", "ControladorBancoDados.insereDados ERRO: idPonto = -1");
        }
    }

    private int verificarUltimoID(){
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

    private long inserirFingerprintDB(Ponto ponto, long id){
        long resultado = -1;
        List<ScanResult> fingerprint = ponto.getFingerprint();


        for(int i = 0; i < fingerprint.size(); i++){

            ContentValues valores = new ContentValues();

            valores.put("BSSID", fingerprint.get(i).BSSID);
            valores.put("INTENSIDADE", fingerprint.get(i).level);
            valores.put("ID", id);

            resultado = db.insert(BancoDados.FINGERPRINT, null, valores);

            if(resultado == -1){
                Log.i("","Erro ao inserir Fingerprint");
            }
        }


        return resultado;
    }

    private long inserirPontoDB(Ponto ponto, long id){
        ContentValues valores = new ContentValues();
        long resultado;


        valores.put("X", ponto.getCoordenadas().getX());
        valores.put("Y", ponto.getCoordenadas().getY());
        valores.put("IDFINGERPRINT", id);
        valores.put("NOME", ponto.getNome());

        resultado = db.insert(BancoDados.PONTOS, null, valores);

        if(resultado == -1){
            Log.i("","Erro ao inserir Pontos");
        }



        return resultado;
    }

    public Cursor consultarCoordenadas(){
        Cursor cursor;
        String[] campos = {BancoDados.ID, "X", "Y"};
        cursor = db.query(BancoDados.PONTOS, campos, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }


        return cursor;
    }


    public Cursor consultarPonto(int id){
        Cursor cursor = null;

        String[] campos = {BancoDados.ID, "NOME", "X", "Y"};
        String whereClause = BancoDados.ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        cursor = db.query(BancoDados.PONTOS, campos, whereClause, whereArgs, null, null, null);

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

        cursor = db.query(BancoDados.FINGERPRINT, campos, whereClause, whereArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor verificarDB(){
        String[] campos = {"_id", "BSSID", "INTENSIDADE"};
        return db.query("FINGERPRINT", campos, null, null, null,  null, null);
    }

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
            db.execSQL("CREATE TABLE " + PONTOS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "X INTEGER," +
                    "Y INTEGER," +
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
            db.execSQL("DROP TABLE IF EXISTS PONTOS");
            db.execSQL("DROP TABLE IF EXISTS FINGERPRINT");
            onCreate(db);
        }

    }

}
