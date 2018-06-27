package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.ImageView;

import java.util.List;

public class Controlador {
    private Context context;
    private ImageView imgview;
    private List<Coordenadas> co;
    private ModuloWiFi moduloWiFi;
    private ControladorBancoDados controladordb;

    public Controlador(Context context, ImageView imgview, List<Coordenadas> co){
        this.co = co;
        this.context = context;
        this.imgview = imgview;
        this.moduloWiFi = new ModuloWiFi(context, this);
        controladordb = new ControladorBancoDados(context);

        inicializarImageView();

    }

    public void cadastrarNovoPonto(int x, int y){

        Ponto ponto = isExistePontoCadastrado(x, y);

        if(ponto != null){
            Log.i("", "Ponto já existe. ID: "+ ponto.getId() + " " + ponto.getCoordenadas().toString());
            return; // TODO
        }

        moduloWiFi.startScan();
        co.add(new Coordenadas(x, y));
    }

    public void finalizarCadastro(List<ScanResult> results){
        if(co.size() > 0) {
            Ponto ponto = new Ponto(co.get(co.size() - 1), "", results);
            adicionarPontoImageView(ponto);
            controladordb.insereDados(ponto);

            Log.i("", "List results: " + results.size());
            Log.i("", ponto.toString());
        }
    }

    public Ponto isExistePontoCadastrado(int x, int y){
        Ponto ponto = null;
        Cursor cursor = controladordb.carregaDados();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                if ((db_x > x -25) && (db_x < x + 25) && (db_y > y - 25) && (db_y < y + 25)){
                    ponto = new Ponto(db_id, new Coordenadas(db_x, db_y));
                }
            }
            cursor.moveToNext();
        }

        return ponto;
    }

    public void adicionarPontoImageView(Ponto ponto){
        co.add(ponto.getCoordenadas());
        desenharImageView();
    }

    public void desenharImageView(){
        Bitmap bmp_planta = BitmapFactory.decodeResource(context.getResources(),R.drawable.planta_predio_2);
        Bitmap tempbm = Bitmap.createBitmap(bmp_planta.getWidth(), bmp_planta.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(tempbm);
        canvas.drawBitmap(bmp_planta, 0, 0, null);

        final Bitmap icone_pin = getBitmapFromVectorDrawable(context,R.drawable.ic_edit_location);

        for (int i = 0; i < co.size(); i++){
            canvas.drawBitmap(icone_pin, co.get(i).getX()- icone_pin.getWidth()/2, co.get(i).getY()- icone_pin.getHeight(), null);
        }

        imgview.setImageBitmap(tempbm);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void inicializarImageView(){
        Cursor cursor = controladordb.carregaDados();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                co.add(new Coordenadas(db_x, db_y));
            }
            cursor.moveToNext();
        }

        desenharImageView();
    }
}
