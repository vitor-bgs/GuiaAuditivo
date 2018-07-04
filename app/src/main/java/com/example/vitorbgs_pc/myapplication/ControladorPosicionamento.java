package com.example.vitorbgs_pc.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorPosicionamento {

    private List<Coordenadas> co;
    private ControladorBancoDados controladordb;
    private Runnable event;
    private Handler handler = new Handler();
    private int delay = 1000; //milliseconds
    private BroadcastReceiver wifiReceiver;
    private WifiManager wifiManager;
    private Mapa mapa;
    private Context context;
    private boolean inicializado = false;


    public ControladorPosicionamento(Context context){
        this.context = context;
        controladordb = new ControladorBancoDados(context);
        co = new ArrayList<Coordenadas>();
        mapa = new Mapa(context, co);

        mapa.inicializarMapa();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                ThreadPosicionamento thread = new ThreadPosicionamento(scanResults);
                thread.start();

                context.unregisterReceiver(this);
            }
        };

        event = new Runnable(){
            public void run(){
                scanWifi();
                handler.postDelayed(this, delay);
            }
        };
    }

    private class ThreadPosicionamento extends Thread{
        private List<ScanResult> scanResults;

        public ThreadPosicionamento(List<ScanResult> scanResults){
            this.scanResults = scanResults;
        }

        @Override
        public void run() {
            exibirPosicao(scanResults);
        }
    }

    public void kill(){
        mapa.recycle();
    }

    public void parar(){
        inicializado = false;
    }

    private void scanWifi(){
        if(inicializado){
            context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
    }

    public void iniciarModoPosicionamento(){
        inicializado = true;
        Cursor cursor = controladordb.verificarDB();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Log.i("", cursor.getString(cursor.getColumnIndex("_id")) + " " + cursor.getString(cursor.getColumnIndex("BSSID")) + " " + cursor.getString(cursor.getColumnIndex("INTENSIDADE")));
            cursor.moveToNext();
        }

        handler.postDelayed(event, delay);
    }

    private Ponto verificarPosicao(List<ScanResult> scanResults){
        Ponto posicao = null;
        Cursor cursor;

        cursor = controladordb.consultarFingerprint(scanResults);


        int[] ids = new int[cursor.getCount()];

        Map<String, String> map = new HashMap<String, String>();

        Log.i("", "========");
        for(int i = 0; i < scanResults.size(); i++){
            String id = scanResults.get(i).BSSID;
            String level = Integer.toString(scanResults.get(i).level);

            Log.i("", "BSSID: " + id + " i: " + level);
        }

        Log.i("", "-----");

        int i = 0;
        while(!cursor.isAfterLast()){

            try {
                String logid = cursor.getString(cursor.getColumnIndex("ID"));
                String logbssid = cursor.getString(cursor.getColumnIndex("BSSID"));
                String loglevel = cursor.getString(cursor.getColumnIndex("INTENSIDADE"));

                Log.i("", "id: " + logid + " bssid: " + logbssid + " i: " + loglevel );

                ids[i] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));

                for(int j = 0; j < scanResults.size(); j++){
                    if(scanResults.get(j).BSSID == logbssid){
                        map.put(logbssid, Integer.toString(Integer.parseInt(loglevel) - scanResults.get(j).level));
                    }
                }
            }
            catch (Exception e){
                Log.i("", e.toString());
            }

            cursor.moveToNext();
            i += 1;
        }

        int id = mode(ids);

        if(id > 0){
            String nome;
            Coordenadas coord;
            int idcoord;

            cursor = controladordb.consultarPonto(id);

            if(cursor != null && cursor.moveToFirst()){
                nome = cursor.getString(cursor.getColumnIndex("NOME"));
                int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));
                coord = new Coordenadas(x, y);
                posicao = new Ponto(id, coord, nome, null);
            }
        }

        return posicao;
    }

    public void exibirPosicao(List<ScanResult> scanResults){
        Ponto ponto = verificarPosicao(scanResults);
        mapa.definirSelecao(ponto);
    }

    public static int mode(int a[]) {
        int maxValue = -1, maxCount = 0;

        for (int i = 0; i < a.length; ++i) {
            int count = 0;
            for (int j = 0; j < a.length; ++j) {
                if (a[j] == a[i]) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = a[i];
            }
        }

        return maxValue;
    }
}
