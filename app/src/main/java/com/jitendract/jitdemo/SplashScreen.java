package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=5000;
    boolean isLoggedIN;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        imgView = findViewById(R.id.imgView);
        Glide.with(this).load(R.drawable.logogif).into(imgView);

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        isLoggedIN =prefs.getBoolean("LoggedIn",false);
        Log.e("prefs", String.valueOf(isLoggedIN));

        if(isLoggedIN){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,HomeScreen.class);
                startActivity(i);
                finish();
            },SPLASH_SCREEN_TIME_OUT);
        }else{
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(i);
                finish();
            },SPLASH_SCREEN_TIME_OUT);
        }

    }
}