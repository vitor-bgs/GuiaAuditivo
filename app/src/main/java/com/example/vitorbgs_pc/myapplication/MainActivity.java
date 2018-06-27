package com.example.vitorbgs_pc.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Coordenadas> co = new ArrayList<Coordenadas>();
        ImageView iv = (ImageView) findViewById(R.id.imageView);

        Controlador controlador = new Controlador(this, iv, co);

        iv.setOnTouchListener(new ImageViewNavigation(this, controlador));
    }
}
