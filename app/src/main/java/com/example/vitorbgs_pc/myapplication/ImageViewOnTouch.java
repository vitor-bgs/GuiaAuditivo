package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ImageViewOnTouch implements View.OnTouchListener {

    float x_down, x_up;
    float y_down, y_up;
    int x_scroll = 0;
    int y_scroll = 0;

    Context context;
    Handler handler = new Handler();
    RunOnPressAndHold event;

    Controller controller;

    int srcWidth;
    int srcHeight;

    public ImageViewOnTouch(Context context, Controller controller){
        this.context = context;
        this.controller = controller;

        srcWidth  = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicWidth();
        srcHeight = context.getResources().getDrawable(R.drawable.planta_predio_2).getIntrinsicHeight();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            x_down = motionEvent.getX();
            y_down = motionEvent.getY();
            x_scroll = view.getScrollX();
            y_scroll = view.getScrollY();

            if(controller.getClass() == ControllerTraining.class){
                event = new RunOnPressAndHold((int) x_down + x_scroll, (int) y_down + y_scroll, controller);
                handler.postDelayed(event, ViewConfiguration.getLongPressTimeout());
            }

            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){

            x_up = motionEvent.getX();
            y_up = motionEvent.getY();
            int x_move = (int)(x_down - x_up);
            int y_move = (int)(y_down - y_up);

            if(x_scroll + x_move < 0){
                x_move = 0;
                x_scroll = 0;
            }
            if(y_scroll + y_move < 0){
                y_move = 0;
                y_scroll = 0;
            }

            if(x_move > 5 || y_move > 5){
                handler.removeCallbacks(event);
            }

            if ((x_move + x_scroll) < (srcWidth - view.getWidth()) && (x_move + x_scroll) >= 0){
                view.setScrollX(x_move + x_scroll);
            }
            if((y_move + y_scroll) < (srcHeight - view.getHeight()) && (y_move + y_scroll) >= 0){
                view.setScrollY(y_move + y_scroll);
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

        private int x;
        private int y;
        private Controller controller;

        public RunOnPressAndHold(int x, int y, Controller controller) {
            this.x = x;
            this.y = y;
            this.controller = controller;
        }

        @Override
        public void run() {
            ControllerTraining control = (ControllerTraining)controller;
            control.registerNewPoint(x, y);
        }
    }
}
