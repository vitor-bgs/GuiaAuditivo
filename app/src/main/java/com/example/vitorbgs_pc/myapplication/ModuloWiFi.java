package com.example.vitorbgs_pc.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class ModuloWiFi {

    private WifiManager wifiManager;
    private WifiReceiver wifireceiver;
    private List<ScanResult> wifiScanList = new ArrayList<ScanResult>();
    private ControladorTreinamento controladorTreinamento;

    public ModuloWiFi(Context context, ControladorTreinamento controladorTreinamento){
        this.controladorTreinamento = controladorTreinamento;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifireceiver = new WifiReceiver(wifiManager);
        context.registerReceiver(wifireceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public boolean startScan(){
        return wifiManager.startScan();
    }

    public List<ScanResult> getWifiScanList(){
        return wifiScanList;
    }

    private class WifiReceiver extends BroadcastReceiver {

        WifiManager mwifi;

        public WifiReceiver(WifiManager mwifi){
            this.mwifi = mwifi;
        }


        public void onReceive(Context c, Intent intent){
            wifiScanList = mwifi.getScanResults();
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < wifiScanList.size(); i++){
                sb.append((i+1) + ". ");
                sb.append(wifiScanList.get(i).BSSID + ": " + wifiScanList.get(i).level);
                sb.append("\n");
            }
            //Log.i("", wifiScanList.size() + sb.toString());
            controladorTreinamento.mostrarDialogo(wifiScanList);
        }
    }

}
