package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Controlador {

    public static final int ACTIVITY_TREINAMENTO = 1;
    public static final int ACTIVITY_NAVIGATION = 0;

    private Context context;
    private ImageView imgview;
    private List<Coordenadas> co;
    private Ponto selecao;
    private ModuloWiFi moduloWiFi;
    private ControladorBancoDados controladordb;
    private int tipoActivity;
    private Handler handler = new Handler();
    private int delay = 10000; //milliseconds

    private boolean habilitarCadastro = false;

    public Controlador(Context context, ImageView imgview, int tipo){
        this.tipoActivity = tipo;
        this.context = context;
        this.imgview = imgview;
        this.moduloWiFi = new ModuloWiFi(context, this);
        co = new ArrayList<Coordenadas>();
        controladordb = new ControladorBancoDados(context);

        inicializarImageView();

    }

    public void iniciarModoPosicionamento(){

        Cursor cursor = controladordb.verificarDB();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Log.i("", cursor.getString(cursor.getColumnIndex("_id")) + " " + cursor.getString(cursor.getColumnIndex("BSSID")) + " " + cursor.getString(cursor.getColumnIndex("INTENSIDADE")));
            cursor.moveToNext();
        }

        moduloWiFi.startScan();
        handler.postDelayed(new Runnable(){
            public void run(){
                moduloWiFi.startScan();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private Ponto verificarPosicao(List<ScanResult> scanResults){
        Ponto posicao = null;
        Cursor cursor;
        int[] ids = new int[scanResults.size()];


        for(int i = 0; i < scanResults.size(); i++){
            cursor = controladordb.verificarFingerprint(scanResults.get(i).BSSID, Integer.toString(scanResults.get(i).level));
            if(cursor != null) {
                try{
                    ids[i] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
                }
                catch (Exception e){

                }
            }
        }

        int id = 0;

        for(int i = 0; i < ids.length; i++){
            if(ids[i] > 0){
                id = ids[i];
            }
        }


        if(id > 0){
            String nome;
            Coordenadas coord;
            int idcoord;

            cursor = controladordb.consultarPonto(id);

            //Log.i("", cursor.getString(cursor.getColumnIndex("IDCOORDENADAS")));

            if(cursor != null && cursor.moveToFirst()){
                nome = cursor.getString(cursor.getColumnIndex("NOME"));
                idcoord = Integer.parseInt(cursor.getString(cursor.getColumnIndex("IDCOORDENADAS")));

                cursor = controladordb.consultarCoordenada(idcoord);

                if(cursor != null && cursor.moveToFirst()){
                    int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                    int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));
                    coord = new Coordenadas(x, y);
                    posicao = new Ponto(id, coord, nome, null);
                }
            }
        }

        return posicao;
    }

    public void exibirPosicao(List<ScanResult> scanResults){

        Ponto ponto = verificarPosicao(scanResults);
        if(ponto != null){
            selecao = ponto;
        }
        desenharImageView();

    }

    public int getTipo(){
        return tipoActivity;
    }

    public void cadastrarNovoPonto(int x, int y){

        Ponto ponto = isExistePontoCadastrado(x, y);

        if(ponto != null){
            Log.i("", "Ponto já existe. ID: "+ ponto.getId() + " " + ponto.getCoordenadas().toString());
            mostrarSelecaoImageView(ponto);
            return;
        }

        selecao = null;
        habilitarCadastro = true;
        moduloWiFi.startScan();
        co.add(new Coordenadas(x, y));
    }

    public void finalizarCadastro(List<ScanResult> results){
        if(co.size() > 0 && habilitarCadastro) {
            Ponto ponto = new Ponto(co.get(co.size() - 1), "", results);
            adicionarPontoImageView(ponto);
            controladordb.insereDados(ponto);
            habilitarCadastro = false;
        }
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

    private void mostrarSelecaoImageView(Ponto ponto){
        selecao = ponto;
        desenharImageView();
    }

    public void desenharImageView(){
        Bitmap bmp_planta = BitmapFactory.decodeResource(context.getResources(),R.drawable.planta_predio_2);
        Bitmap tempbm = Bitmap.createBitmap(bmp_planta.getWidth(), bmp_planta.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(tempbm);
        canvas.drawBitmap(bmp_planta, 0, 0, null);

        Bitmap icone_pin = getBitmapFromVectorDrawable(context,R.drawable.ic_edit_location);

        for (int i = 0; i < co.size(); i++){
            if(selecao == null || selecao.getCoordenadas() != co.get(i)){
                canvas.drawBitmap(icone_pin, co.get(i).getX()- icone_pin.getWidth()/2, co.get(i).getY()- icone_pin.getHeight(), null);
            }
        }

        if(selecao != null){
            Bitmap icone_selecao = getBitmapFromVectorDrawable(context, R.drawable.ic_place_blue);
            canvas.drawBitmap(icone_selecao, selecao.getCoordenadas().getX() - icone_selecao.getWidth()/2, selecao.getCoordenadas().getY() - icone_selecao.getHeight(), null);
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
        Cursor cursor = controladordb.consultarCoordenadas();

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


    public static int mode(int a[]) {
        int maxValue = -1, maxCount = 0;

        for (int i = 0; i < a.length; ++i) {
            int count = 0;
            for (int j = 0; j < a.length; ++j) {
                if (a[j] == a[i]) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = a[i];
            }
        }

        return maxValue;
    }
}
