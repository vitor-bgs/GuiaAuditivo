package com.example.vitorbgs_pc.myapplication;

public class Coordenadas {
    private int x;
    private int y;

    public Coordenadas(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    @Override
    public String toString(){
        return "Coordenadas: [" + x + ", " + y + "]";
    }
}
