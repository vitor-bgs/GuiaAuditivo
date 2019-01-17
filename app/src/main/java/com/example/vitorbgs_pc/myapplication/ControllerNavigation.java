package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControllerNavigation extends Controller {

    // GLOBAL
    private ControllerDatabase controllerDatabase;
    private VoiceSynthesizer tts;
    private WiFiModule wifiModule; //maybe it shouldn't be here


    // CONSTRUCTOR
    public ControllerNavigation(Context context){
        this.wifiModule = new WiFiModule(context, this);
        controllerDatabase = new ControllerDatabase(context);
        tts = new VoiceSynthesizer(context);
    }

    // CONTROLLER FUNCTIONS
    public void wifiScanReceived(List<ScanResult> scanResults){
        checkPosition(scanResults);
    }

    public void finalize(){
        wifiModule.finalizeWifiModule();
    }

    //NAVIGATION FUNCTIONS
    public void startNavigation(){
        Cursor cursor = controllerDatabase.verificarDB();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Log.i("", cursor.getString(cursor.getColumnIndex("_id")) + " " + cursor.getString(cursor.getColumnIndex("BSSID")) + " " + cursor.getString(cursor.getColumnIndex("INTENSIDADE")));
            cursor.moveToNext();
        }
    }

    private MapPoint checkPosition(List<ScanResult> scanResults){
        MapPoint position = null;
        Cursor cursor;

        cursor = controllerDatabase.consultarFingerprint(scanResults);


        int[] ids = new int[cursor.getCount()];

        java.util.Map map = new HashMap<String, String>();

        //Log
        Log.i("", "========");
        for(int i = 0; i < scanResults.size(); i++){
            String id = scanResults.get(i).BSSID;
            String level = Integer.toString(scanResults.get(i).level);

            Log.i("WiFi", "BSSID: " + id + " i: " + level);
        }
        //End Log

        Log.i("", "-----");

        int i = 0;
        while(!cursor.isAfterLast()){

            try {
                String logid = cursor.getString(cursor.getColumnIndex("ID"));
                String logbssid = cursor.getString(cursor.getColumnIndex("BSSID"));
                String loglevel = cursor.getString(cursor.getColumnIndex("INTENSIDADE"));

                Log.i("DB", "id: " + logid + " bssid: " + logbssid + " i: " + loglevel );

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
        //Fim Log

        int id = mode(ids);

        if(id > 0){
            String nome;
            cursor = controllerDatabase.consultarPonto(id);

            if(cursor != null && cursor.moveToFirst()){
                nome = cursor.getString(cursor.getColumnIndex("NOME"));
                int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));
                position = new MapPoint(id, nome, x, y,null);
                tts.speak(nome);
            }
        }

        return position;
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
