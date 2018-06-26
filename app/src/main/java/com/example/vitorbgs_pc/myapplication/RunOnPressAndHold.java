package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import java.util.List;

public class RunOnPressAndHold implements Runnable{

    private int x = 0;
    private int y = 0;
    private ImageView imgview;
    private Context context;
    private List<Coordenadas> co;

    public RunOnPressAndHold(ImageView img, List<Coordenadas> co, Context context) {
        this.imgview = img;
        this.context = context;
        this.co = co;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        co.add(new Coordenadas(x, y));

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
}
