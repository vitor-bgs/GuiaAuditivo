package com.example.vitorbgs_pc.myapplication;
import android.net.wifi.ScanResult;

import java.util.List;

public abstract class Controller {

    public abstract void wifiScanReceived(List<ScanResult> scanResults);

    public abstract void finalize();
}
