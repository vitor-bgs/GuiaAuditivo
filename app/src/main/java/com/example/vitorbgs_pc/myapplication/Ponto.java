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

    public int getId(){
        return id;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public List<ScanResult> getFingerprint(){
        return fingerprint;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    @Override
    public String toString(){
        return "ID: " + id + "Nome: " + nome + ", " + coordenadas + ", " + fingerprint.toString();
    }


}
