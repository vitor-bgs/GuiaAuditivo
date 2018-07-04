package com.example.vitorbgs_pc.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class ActivityNavigation extends AppCompatActivity {

    ControladorPosicionamento controlador;
    ImageViewNavigation ivn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Guia Auditivo");

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        ivn = new ImageViewNavigation(this, ImageViewNavigation.ACTIVITY_NAVIGATION);

        iv.setOnTouchListener(ivn);

        controlador = new ControladorPosicionamento(this);
        controlador.iniciarModoPosicionamento();

        toolbar.setOnMenuItemClickListener(new MenuOnClick());


    }

    private class MenuOnClick implements Toolbar.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(ActivityNavigation.this, toolbar, Gravity.RIGHT);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.menu_popup, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //showing popup menu
                    Intent myIntent = new Intent(ActivityNavigation.this, ActivityTreinamento.class);
                    //controlador.kill();
                    startActivity(myIntent);
                    return true;
                }
            });

            popup.show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }
}
