package com.example.vitorbgs_pc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;


public class ActivityTreinamento extends AppCompatActivity {

    ImageViewNavigation ivn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treinamento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle("Treinamento");

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        ivn = new ImageViewNavigation(this, ImageViewNavigation.ACTIVITY_TREINAMENTO);

        iv.setOnTouchListener(ivn);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(ActivityTreinamento.this, ActivityNavigation.class);
                //ivn.kill();
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_treinamento, menu);
        return true;
    }
}
