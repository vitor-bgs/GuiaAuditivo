package com.example.vitorbgs_pc.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

public class WiFiModule {

    private final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    private WifiManager wifiManager;
    private Controller controller;
    private Context context;
    private WifiReceiver wifiReceiver;

    public WiFiModule(Context context, Controller controller){
        this.controller = controller;
        this.context = context;
        initializeWifiModule();
    }

    public boolean startScan(){

        return wifiManager.startScan();
    }

    public void initializeWifiModule(){
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver(wifiManager);
        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startScan();
    }

    public void finalizeWifiModule(){
        context.unregisterReceiver(wifiReceiver);
    }

    private class WifiReceiver extends BroadcastReceiver {

        WifiManager wifiManager;

        public WifiReceiver(WifiManager wifiManager){
            this.wifiManager = wifiManager;
        }


        public void onReceive(Context c, Intent intent){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

            }else{
                controller.wifiScanReceived(wifiManager.getScanResults());
            }

            //log
            //log(wifiManager.getScanResults());
        }

        private void log(List<ScanResult> scanResults){
            //Log
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < wifiScanList.size(); i++){
                stringBuilder.append((i+1) + ". ");
                stringBuilder.append(wifiScanList.get(i).BSSID + ": " + wifiScanList.get(i).level);
                stringBuilder.append("\n");
            }
            Log.i("", wifiScanList.size() + stringBuilder.toString());
        }
    }

}
