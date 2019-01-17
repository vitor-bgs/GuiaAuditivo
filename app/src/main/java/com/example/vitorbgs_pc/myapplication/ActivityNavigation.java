package com.example.vitorbgs_pc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class ActivityNavigation extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ControllerNavigation controller = new ControllerNavigation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Guia Auditivo");

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setOnTouchListener(new ImageViewOnTouch(this, controller));


        controller.startNavigation();

        toolbar.setOnMenuItemClickListener(new MenuOnClick(){
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                controller.finalize();
                return super.onMenuItemClick(menuItem);
            }
        });
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
                    Intent myIntent = new Intent(ActivityNavigation.this, ActivityTraining.class);
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
