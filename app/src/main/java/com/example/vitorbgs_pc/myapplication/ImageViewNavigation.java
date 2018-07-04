package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ImageViewNavigation implements View.OnTouchListener {


    public static final int ACTIVITY_TREINAMENTO = 1;
    public static final int ACTIVITY_NAVIGATION = 0;

    float xdown, xup;
    float ydown, yup;
    int xscroll = 0;
    int yscroll = 0;

    Context context;
    Handler handler = new Handler();
    RunOnPressAndHold event;
    int tipoActivity;

    ControladorTreinamento controladorTreinamento;

    int srcWidth;
    int srcHeight;

    public ImageViewNavigation(Context context, int tipoActivity){
        this.context = context;
        this.tipoActivity = tipoActivity;

        controladorTreinamento = new ControladorTreinamento(context);

        srcWidth  = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicWidth();
        srcHeight = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicHeight();
    }

    public void kill(){
        controladorTreinamento.kill();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            xdown = motionEvent.getX();
            ydown = motionEvent.getY();
            xscroll = view.getScrollX();
            yscroll = view.getScrollY();

            if(tipoActivity == ImageViewNavigation.ACTIVITY_TREINAMENTO){
                event = new RunOnPressAndHold((int) xdown + xscroll, (int) ydown + yscroll, controladorTreinamento);
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

            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            handler.removeCallbacks(event);
            return true;
        }

        return false;
    }

    private class RunOnPressAndHold implements Runnable{

        private int x = 0;
        private int y = 0;
        private ControladorTreinamento controladorTreinamento;

        public RunOnPressAndHold(int x, int y, ControladorTreinamento controladorTreinamento) {
            this.x = x;
            this.y = y;
            this.controladorTreinamento = controladorTreinamento;
        }

        @Override
        public void run() {
            controladorTreinamento.cadastrarNovoPonto(x, y);
        }
    }
}
