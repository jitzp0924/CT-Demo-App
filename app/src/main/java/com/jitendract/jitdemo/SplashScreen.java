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
import com.clevertap.android.sdk.CleverTapAPI;

import java.util.Date;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

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

//        CleverTapAPI.createNotificationChannel(getApplicationContext(),"JitDemo","JitDemo","JitDemo", NotificationManager.IMPORTANCE_MAX,true);
        
//        Boolean val = permissonAccess();
//        if (val){
//            initClevertapGeofence();
//        }
//        else {}

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        isLoggedIN =prefs.getBoolean("LoggedIn",false);
        Log.e("prefs", String.valueOf(isLoggedIN));

        int SPLASH_SCREEN_TIME_OUT = 5000;
        if(isLoggedIN){
            new Handler().postDelayed(() -> {
                CleverTapAPI dfI = CleverTapAPI.getDefaultInstance(getApplicationContext());
                HashMap<String, Object> nt = new HashMap<String, Object>();
                nt.put("Date",new Date());
                nt.put("Screen","Splash");
                dfI.pushEvent("Splash Screen",nt);
                Intent i = new Intent(SplashScreen.this,HomeScreen2.class);
                startActivity(i);
                finish();
            }, SPLASH_SCREEN_TIME_OUT);
        }else{
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(i);
                finish();
            }, SPLASH_SCREEN_TIME_OUT);
        }

    }

//    private void initClevertapGeofence() {
//    }

//    private Boolean permissonAccess() {
//        final Boolean[] val = new Boolean[1];
//
//        ActivityResultLauncher<String[]> locationPermissionRequest =
//                registerForActivityResult(new ActivityResultContracts
//                                .RequestMultiplePermissions(), result -> {
//                            Boolean fineLocationGranted = result.getOrDefault(
//                                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
//                            Boolean coarseLocationGranted = result.getOrDefault(
//                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
//                            if (fineLocationGranted != null && fineLocationGranted) {
//                                // Precise location access granted.
//                                val[0] = true;
//                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
//                                // Only approximate location access granted.
//                                val[0] = true;
//                            } else {
//                                // No location access granted.
//                                val[0] = false;
//                            }
//                        }
//                );
//
//        locationPermissionRequest.launch(new String[] {
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        });
//
//        return val[0];
//
//    }
}