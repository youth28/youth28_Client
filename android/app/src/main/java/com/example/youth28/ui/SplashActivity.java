package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    ImageView img2, img8, imgC, imgCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img2 = findViewById(R.id.img2);
        img8 = findViewById(R.id.img8);
        imgC = findViewById(R.id.imgC);
        imgCC = findViewById(R.id.imgCC);

        Animation appear2;
        appear2 = AnimationUtils.loadAnimation(this, R.anim.l2_anim);
        img2.setAnimation(appear2);

        Animation appear8;
        appear8 = AnimationUtils.loadAnimation(this, R.anim.l8_anim);
        img8.setAnimation(appear8);

        Animation appearC;
        appearC = AnimationUtils.loadAnimation(this, R.anim.lcc_anim);
        imgCC.setAnimation(appearC);

        Animation appearCC;
        appearCC = AnimationUtils.loadAnimation(this, R.anim.lc_anim);
        imgC.setAnimation(appearCC);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2700);
    }
}