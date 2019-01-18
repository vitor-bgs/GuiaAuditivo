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

import java.util.ArrayList;
import java.util.List;

public class Map {

    private final int X = 0;
    private final int Y = 1;

    private Context context;
    private List<int[]> co;

    private ImageView imageView;

    public Map(Context context){
        this.context = context;
        this.co = new ArrayList<int[]>();
        imageView = (ImageView) ((Activity) context).findViewById(R.id.imageView);
        initializeMap();
    }

    public void addPoint(int[] point){
        co.add(point);
        drawMap(null);
    }

    public void setSelection(MapPoint mapPoint){
        drawMap(mapPoint);
    }

    public void drawMap(MapPoint select){
        final MapPoint selection = select;

        ((Activity) context).runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Bitmap selectionIcon = getBitmapFromVectorDrawable(context, R.drawable.ic_place_blue);
                Bitmap pinIcon = getBitmapFromVectorDrawable(context,R.drawable.ic_edit_location);
                Bitmap plantBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.planta_predio_2);


                Bitmap tempbm = Bitmap.createBitmap(plantBitmap.getWidth(), plantBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(tempbm);
                canvas.drawBitmap(plantBitmap, 0, 0, null);

                for (int i = 0; i < co.size(); i++){
                    if(selection == null || selection.getCoordinates() != co.get(i)){
                        canvas.drawBitmap(pinIcon, co.get(i)[X] - pinIcon.getWidth()/2, co.get(i)[Y]- pinIcon.getHeight(), null);
                    }
                }
                if(selection != null){
                    canvas.drawBitmap(selectionIcon, selection.getX() - selectionIcon.getWidth()/2, selection.getY() - selectionIcon.getHeight(), null);
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

    public void initializeMap(){
        ControllerDatabase controllerDatabase = new ControllerDatabase(context);
        Cursor cursor = controllerDatabase.getAllPoints();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex("_id"))!= null){
                int db_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                int db_x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("X")));
                int db_y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Y")));

                co.add(new int[] {db_x, db_y});
            }
            cursor.moveToNext();
        }

        drawMap(null);
    }
}
