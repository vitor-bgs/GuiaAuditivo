package com.example.vitorbgs_pc.myapplication;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import java.util.List;

public class Mapa {

    Context context;
    Ponto selecao = null;
    ImageView imageView;
    List<Coordenadas> co;
    Bitmap icone_selecao;
    Bitmap icone_pin;
    Bitmap bmp_planta;


    public Mapa(Context context, List<Coordenadas> coordenadas){
        this.context = context;
        this.imageView = ((Activity) context).findViewById(R.id.imageView);
        this.co = coordenadas;
        icone_selecao = getBitmapFromVectorDrawable(context, R.drawable.ic_place_blue);
        icone_pin = getBitmapFromVectorDrawable(context,R.drawable.ic_edit_location);
        bmp_planta = BitmapFactory.decodeResource(context.getResources(),R.drawable.planta_predio_2);
    }

    public void recycle(){
        icone_pin.recycle();
        icone_selecao.recycle();
        bmp_planta.recycle();
    }

    private void inicializarImagens(){
        icone_selecao = getBitmapFromVectorDrawable(context, R.drawable.ic_place_blue);
        icone_pin = getBitmapFromVectorDrawable(context,R.drawable.ic_edit_location);
        bmp_planta = BitmapFactory.decodeResource(context.getResources(),R.drawable.planta_predio_2);
    }


    public void adicionarPontoImageView(Ponto ponto){
        co.add(ponto.getCoordenadas());
        desenharMapa();
    }

    public void definirSelecao(Ponto ponto){
        this.selecao = ponto;
        desenharMapa();
    }

    public void desenharMapa(){

        if(icone_selecao.isRecycled() || icone_pin.isRecycled() ||  bmp_planta.isRecycled() ){
            inicializarImagens();
        }

        ((Activity) context).runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Bitmap tempbm = Bitmap.createBitmap(bmp_planta.getWidth(), bmp_planta.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(tempbm);
                canvas.drawBitmap(bmp_planta, 0, 0, null);

                for (int i = 0; i < co.size(); i++){
                    if(selecao == null || selecao.getCoordenadas() != co.get(i)){
                        canvas.drawBitmap(icone_pin, co.get(i).getX()- icone_pin.getWidth()/2, co.get(i).getY()- icone_pin.getHeight(), null);
                    }
                }
                if(selecao != null){
                    canvas.drawBitmap(icone_selecao, selecao.getCoordenadas().getX() - icone_selecao.getWidth()/2, selecao.getCoordenadas().getY() - icone_selecao.getHeight(), null);
                }

                imageView.setImageBitmap(tempbm);
            }
        });
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

    public void inicializarMapa(){
        ControladorBancoDados controladorBancoDados = new ControladorBancoDados(context);
        Cursor cursor = controladorBancoDados.consultarCoordenadas();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                co.add(new Coordenadas(db_x, db_y));
            }
            cursor.moveToNext();
        }

        desenharMapa();
    }
}
