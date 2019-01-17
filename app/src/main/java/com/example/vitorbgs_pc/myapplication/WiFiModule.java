package com.example.vitorbgs_pc.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WiFiModule {

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
            controller.wifiScanReceived(wifiManager.getScanResults());

            //log
            log(wifiManager.getScanResults());
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
