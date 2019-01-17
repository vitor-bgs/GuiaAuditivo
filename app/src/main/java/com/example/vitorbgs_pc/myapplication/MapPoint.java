package com.example.vitorbgs_pc.myapplication;

import android.net.wifi.ScanResult;
import java.util.List;



public class MapPoint {
    private int id;
    private String name;
    private int x;
    private int y;
    private List<ScanResult> fingerprint;

    public MapPoint(int id, String name, int x, int y, List<ScanResult> fingerprint){
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.fingerprint = fingerprint;
    }

    // Getters

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int[] getCoordinates() {
        return new int[] {x, y};
    }

    public int getX(){
        return  x;
    }

    public int getY(){
        return y;
    }

    public List<ScanResult> getFingerprint(){
        return fingerprint;
    }

}
