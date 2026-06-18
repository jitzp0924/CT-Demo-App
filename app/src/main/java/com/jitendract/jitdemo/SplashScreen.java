package com.jitendract.jitdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import java.util.Date;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // Load the animated GIF logo
        ImageView imgView = findViewById(R.id.imgView);
        Glide.with(this).load(R.drawable.logogif).into(imgView);

        boolean isLoggedIn = getSharedPreferences("Login", MODE_PRIVATE)
                .getBoolean("LoggedIn", false);
        Log.d("SplashScreen", "isLoggedIn=" + isLoggedIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isLoggedIn) {
                trackSplashEvent();
                startActivity(HomeRouter.getHomeIntent(this));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }, SPLASH_TIMEOUT_MS);
    }

    private void trackSplashEvent() {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (ct == null) return;
        HashMap<String, Object> props = new HashMap<>();
        props.put("Date", new Date());
        props.put("Screen", "Splash");
        ct.pushEvent("Splash Screen", props);
    }
}
