package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.util.TimingLogger;

import java.util.ArrayList;
import java.util.List;

public class ControllerNavigation extends Controller {

    // GLOBAL
    private ControllerDatabase controllerDatabase;
    private VoiceSynthesizer tts;
    private WiFiModule wifiModule; //maybe it shouldn't be here
    private Map map;
    private Cursor allPoints;
    private Cursor allFingerprints;
    Handler handler = new Handler();


    // CONSTRUCTOR
    public ControllerNavigation(Context context){
        this.wifiModule = new WiFiModule(context, this);
        this.controllerDatabase = new ControllerDatabase(context);
        this.tts = new VoiceSynthesizer(context);
        this.map = new Map(context);

        initializeNavigation();
        wifiModule.startScan();
    }

    // CONTROLLER FUNCTIONS
    public void wifiScanReceived(List<ScanResult> scanResults){
        MapPoint currentPosition = checkPosition(scanResults);
        map.drawMap(currentPosition);

        //log(scanResults);
    }

    public void finalize(){
        wifiModule.finalizeWifiModule();
        handler.removeCallbacksAndMessages(null);
    }

    //NAVIGATION FUNCTIONS
    private void initializeNavigation(){
        allPoints = controllerDatabase.getAllPoints();
        allFingerprints = controllerDatabase.getAllFingerprints();

        allPoints.moveToFirst();

        while(!allPoints.isAfterLast()){
            String id, name, x, y, fingerprint;
            id = allPoints.getString(allPoints.getColumnIndex("_id"));
            name = allPoints.getString(allPoints.getColumnIndex("NAME"));
            x = allPoints.getString(allPoints.getColumnIndex("X"));
            y = allPoints.getString(allPoints.getColumnIndex("Y"));
            fingerprint = allPoints.getString(allPoints.getColumnIndex("IDFINGERPRINT"));

            Log.i("Point", String.format("id: %s | name: %s | x: %s | y: %s | id_f: %s", id, name, x, y, fingerprint));
            allPoints.moveToNext();
        }


        allFingerprints.moveToFirst();

        while(!allFingerprints.isAfterLast()){
            String id, bssid, level, fingerprint;
            id = allFingerprints.getString(allFingerprints.getColumnIndex("_id"));
            bssid = allFingerprints.getString(allFingerprints.getColumnIndex("BSSID"));
            level = allFingerprints.getString(allFingerprints.getColumnIndex("INTENSITY"));
            fingerprint = allFingerprints.getString(allFingerprints.getColumnIndex("IDFINGERPRINT"));

            Log.i("Fingerprint", String.format("id: %s | BSSID: %s | level: %s, idf: %s", id, bssid, level, fingerprint));
            allFingerprints.moveToNext();
        }


        handler.postDelayed(new Runnable(){
            public void run(){
                wifiModule.startScan();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private MapPoint checkPosition(List<ScanResult> scanResults){
        MapPoint currentPosition = null;

        List<int[]> idlist = getClosestIndexes(scanResults);

        int[] ids = new int[idlist.size()];

        for(int i = 0; i < idlist.size(); i++){
            ids[i] = idlist.get(i)[0];
        }

        int id = mode(ids);

        if(id > 0){
            String name;
            Cursor cursor = controllerDatabase.getPoint(id);

            if(cursor != null && cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndex("NAME"));
                int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));
                currentPosition = new MapPoint(id, name, x, y,null);
                tts.speak(name);
            }
        }

        return currentPosition;
    }

    private List<int[]> getClosestIndexes(List<ScanResult> scanResults){
        List<int[]> idlist = new ArrayList<int[]>();

        TimingLogger timmings = new TimingLogger("tag", "test");
        timmings.addSplit("Start");

        for(int i = 0; i < scanResults.size(); i++){
            String scanBssid = scanResults.get(i).BSSID;
            int scanLevel = scanResults.get(i).level;

            allFingerprints.moveToFirst();
            while(!allFingerprints.isAfterLast()){
                String bssid = allFingerprints.getString(allFingerprints.getColumnIndex("BSSID"));
                int level = allFingerprints.getInt(allFingerprints.getColumnIndex("INTENSITY"));
                if(scanBssid.equals(bssid)){
                    if(scanLevel > level - 5){
                        if(scanLevel < level + 5){
                            idlist.add(new int[] {
                                    Integer.parseInt(allFingerprints.getString(allFingerprints.getColumnIndex("IDFINGERPRINT"))),
                                    Math.abs(Integer.parseInt(allFingerprints.getString(allFingerprints.getColumnIndex("INTENSITY"))) - scanLevel)
                            });
                        }
                    }
                }

                allFingerprints.moveToNext();
            }
        }

        timmings.addSplit("End");

        timmings.dumpToLog();

        return idlist;
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

    private void log(List<ScanResult> scanResults){
        //Log
        Log.i("", "========");
        for(int i = 0; i < scanResults.size(); i++){
            String id = scanResults.get(i).BSSID;
            String level = Integer.toString(scanResults.get(i).level);

            Log.i("WiFi", "BSSID: " + id + " i: " + level);
        }
        //End Log

        Log.i("", "-----");
    }
}
