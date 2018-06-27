package com.example.vitorbgs_pc.myapplication;

import android.net.wifi.ScanResult;

import java.util.List;

public class Ponto {
    private int id;
    private Coordenadas coordenadas;
    private String nome;
    private List<ScanResult> fingerprint;

    public Ponto(int id, Coordenadas coordenadas, String nome, List<ScanResult> fingerprint){
        this.id = id;
        this.coordenadas = coordenadas;
        this.nome = nome;
        this.fingerprint = fingerprint;
    }

    public Ponto(Coordenadas coordenadas, String nome, List<ScanResult> fingerprint){
        this.id = -1;
        this.coordenadas = coordenadas;
        this.nome = nome;
        this.fingerprint = fingerprint;
    }

    public Ponto(int id, Coordenadas coordenadas){
        this.id = id;
        this.coordenadas = coordenadas;
        this.fingerprint = null;
    }

    public int getId(){
        return id;
    }


    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    @Override
    public String toString(){
        return "ID: " + id + "Nome: " + nome + ", " + coordenadas + ", " + fingerprint.toString();
    }
}
