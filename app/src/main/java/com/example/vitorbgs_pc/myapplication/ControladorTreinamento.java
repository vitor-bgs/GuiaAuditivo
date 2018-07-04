package com.example.vitorbgs_pc.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ControladorTreinamento {

    private List<Coordenadas> co;
    private ModuloWiFi moduloWiFi;
    private ControladorBancoDados controladordb;

    private boolean habilitarCadastro = false;

    Context context;

    Mapa mapa;

    public ControladorTreinamento(Context context){
        this.moduloWiFi = new ModuloWiFi(context, this);
        this.context = context;

        co = new ArrayList<Coordenadas>();
        controladordb = new ControladorBancoDados(context);

        mapa = new Mapa(context, co);
        mapa.inicializarMapa();


    }

    private class DialogoConfirmacao {
        AlertDialog alertDialog;
        ControladorTreinamento controladorTreinamento;

        public DialogoConfirmacao(Context context, final List<ScanResult> results, ControladorTreinamento controladorTreinamento){
            this.controladorTreinamento = controladorTreinamento;
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.nome_ponto_dialog, null);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.input);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            alertDialogBuilder.setTitle("Nome (Opcional):");

            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String nome = userInput.getText().toString();
                    Ponto ponto = new Ponto(-1, co.get(co.size()-1), nome, results);
                    finalizarCadastro(ponto);
                }
            }).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
        }

        public void mostrarDialogo(){
            alertDialog.show();
        }
    }

    public void kill(){
        mapa.recycle();
    }

    public void cadastrarNovoPonto(int x, int y){
        Ponto ponto = isExistePontoCadastrado(x, y);

        if(ponto != null){
            Log.i("", "Ponto já existe. ID: "+ ponto.getId() + " " + ponto.getCoordenadas().toString());
            mapa.definirSelecao(ponto);
            return;
        }

        habilitarCadastro = true;
        moduloWiFi.startScan();
        mapa.adicionarPontoImageView(new Ponto(-1, new Coordenadas(x, y), null, null));
        co.add(new Coordenadas(x, y));
    }

    public void mostrarDialogo(List<ScanResult> results){
        if(co.size() > 0 && habilitarCadastro) {
            habilitarCadastro = false;
            DialogoConfirmacao dc = new DialogoConfirmacao(context, results, this);
            dc.mostrarDialogo();
        }
    }

    private void finalizarCadastro(Ponto ponto){
        habilitarCadastro = false;
        controladordb.insereDados(ponto);
    }

    public Ponto isExistePontoCadastrado(int x, int y){
        Ponto ponto = null;
        Cursor cursor = controladordb.consultarCoordenadas();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                if ((db_x > x -25) && (db_x < x + 25) && (db_y > y - 30) && (db_y < y + 60)){
                    ponto = new Ponto(db_id, new Coordenadas(db_x, db_y), null, null);
                }
            }
            cursor.moveToNext();
        }

        return ponto;
    }
}
