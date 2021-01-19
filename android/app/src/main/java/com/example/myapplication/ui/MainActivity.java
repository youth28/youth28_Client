package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnMenu, btnSearch;
    DrawerLayout drawer_layout;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenu = findViewById(R.id.btnMenu);
        btnSearch = findViewById(R.id.btnSearch);
        drawer_layout = findViewById(R.id.drawer_layout);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        btnMenu.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMenu:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(this, RoomMakeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers();
        } else{
            super.onBackPressed();
        }
    }
}