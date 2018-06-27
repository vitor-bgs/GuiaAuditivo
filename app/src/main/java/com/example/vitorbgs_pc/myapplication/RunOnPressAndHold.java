package com.example.vitorbgs_pc.myapplication;

public class RunOnPressAndHold implements Runnable{

    private int x = 0;
    private int y = 0;
    private Controlador controlador;

    public RunOnPressAndHold(int x, int y, Controlador controlador) {
        this.x = x;
        this.y = y;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        controlador.cadastrarNovoPonto(x, y);
    }
}
