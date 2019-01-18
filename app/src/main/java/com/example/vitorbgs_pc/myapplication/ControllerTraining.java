package com.example.vitorbgs_pc.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ControllerTraining extends Controller {

    // GLOBAL
    private List<int[]> co;
    private WiFiModule wifiModule;
    private ControllerDatabase controllerDB;
    private boolean registering = false;
    private Context context;
    private Map map;

    // CONSTRUCTOR
    public ControllerTraining(Context context){
        this.wifiModule = new WiFiModule(context, this);
        this.context = context;

        co = new ArrayList<int[]>();
        controllerDB = new ControllerDatabase(context);

        map = new Map(context);
    }

    // CONTROLLER FUNCTIONS
    public void wifiScanReceived(List<ScanResult> scanResults){
        showDialog(scanResults);
    }

    public void finalize(){
        wifiModule.finalizeWifiModule();
    }


    // TRAINING FUNCTIONS
    public void registerNewPoint(int x, int y){
        MapPoint mapPoint = isPointRegistered(x, y);

        if(mapPoint != null){
            Log.i("", "MapPoint already exists. ID: "+ mapPoint.getId() + " " + mapPoint.getCoordinates().toString());
            map.setSelection(mapPoint);
            return;
        }

        registering = true;
        wifiModule.startScan();
        map.addPoint(new int[] {x, y});
        co.add(new int[] {x, y});
    }

    public void showDialog(List<ScanResult> results){
        if(co.size() > 0 && registering) {
            registering = false;
            ConfirmationDialog dc = new ConfirmationDialog(context, results);
            dc.ShowDialog();
        }
    }

    private void finalizeRegistering(MapPoint mapPoint){
        controllerDB.insertData(mapPoint);
        registering = false;
    }

    public MapPoint isPointRegistered(int x, int y){
        MapPoint mapPoint = null;
        Cursor cursor = controllerDB.getAllPoints();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                if ((db_x > x -25) && (db_x < x + 25) && (db_y > y - 30) && (db_y < y + 60)){
                    mapPoint = new MapPoint(db_id,null, db_x, db_y, null);
                }
            }
            cursor.moveToNext();
        }

        return mapPoint;
    }



    private class ConfirmationDialog {
        AlertDialog alertDialog;

        public ConfirmationDialog(Context context, final List<ScanResult> results){
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.nome_ponto_dialog, null);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.input);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            alertDialogBuilder.setTitle("Nome (Opcional):");

            String message = "";

            for(int i = 0; i < results.size(); i++){
                message += String.format("BSSID: %s, I: %s \n", results.get(i).BSSID, results.get(i).level);
            }

            alertDialogBuilder.setMessage(message);

            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String nome = userInput.getText().toString();
                    int[] coord = co.get(co.size() -1);
                    MapPoint mapPoint = new MapPoint(-1, nome, coord[0], coord[1], results);
                    finalizeRegistering(mapPoint);
                }
            }).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
        }

        public void ShowDialog(){
            alertDialog.show();
        }
    }
}
