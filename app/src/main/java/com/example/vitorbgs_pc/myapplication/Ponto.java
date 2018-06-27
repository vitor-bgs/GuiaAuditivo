package com.example.vitorbgs_pc.myapplication;

import android.net.wifi.ScanResult;

import java.util.List;

public class Ponto {
    private Coordenadas coordenadas;
    private String nome;
    private List<ScanResult> fingerprint;

    public Ponto(Coordenadas coordenadas, String nome, List<ScanResult> fingerprint){
        this.coordenadas = coordenadas;
        this.nome = nome;
        this.fingerprint = fingerprint;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    @Override
    public String toString(){
        return "Nome: " + nome + ", " + coordenadas + ", " + fingerprint.toString();
    }
}
