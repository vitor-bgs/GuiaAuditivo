package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ImageViewNavigation implements View.OnTouchListener {

    float xdown, xup;
    float ydown, yup;
    int xscroll = 0;
    int yscroll = 0;

    Context context;
    Controlador controlador;
    Handler handler = new Handler();
    RunOnPressAndHold event;

    int srcWidth;
    int srcHeight;

    public ImageViewNavigation(Context context, Controlador controlador){
        this.context = context;
        this.controlador = controlador;


        srcWidth  = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicWidth();
        srcHeight = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicHeight();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            xdown = motionEvent.getX();
            ydown = motionEvent.getY();
            xscroll = view.getScrollX();
            yscroll = view.getScrollY();

            if(controlador.getTipo() == Controlador.ACTIVITY_TREINAMENTO){
                event = new RunOnPressAndHold((int) xdown + xscroll, (int) ydown + yscroll, controlador);
                handler.postDelayed(event, ViewConfiguration.getLongPressTimeout());

            }
            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){

            xup = motionEvent.getX();
            yup = motionEvent.getY();
            int xmove = (int)(xdown - xup);
            int ymove = (int)(ydown - yup);

            if(xscroll + xmove < 0){
                xmove = 0;
                xscroll = 0;
            }
            if(yscroll + ymove < 0){
                ymove = 0;
                yscroll = 0;
            }

            if(xmove > 5 || ymove > 5){
                handler.removeCallbacks(event);
            }

            if ((xmove + xscroll) < (srcWidth - view.getWidth()) && (xmove + xscroll) >= 0){
                view.setScrollX(xmove + xscroll);
            }
            if((ymove + yscroll) < (srcHeight - view.getHeight()) && (ymove + yscroll) >= 0){
                view.setScrollY(ymove + yscroll);
            }

            String text = String.format("DOWN[X: %s, Y: %s]\n\r" +
                    "UP[X: %s, Y: %s]\n\r" +
                    "MOVE[X: %s, Y: %s]\n\r" +
                    "M+S[X: %s, Y: %s]", xdown, ydown, xup, yup, srcWidth, srcHeight, xmove + xscroll, ymove + yscroll);
            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            handler.removeCallbacks(event);
            return true;
        }

        return false;
    }
}
