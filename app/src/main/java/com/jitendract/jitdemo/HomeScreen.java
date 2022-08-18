package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomeScreen extends AppCompatActivity {

    FloatingActionButton fab;
    Boolean isAllFabsVisible;
    TextView webViewText,inboxText,inappText,nativeDisplayText;
    FloatingActionButton inappFab,inboxFab,webView,nativeDisplay;
    boolean isLoggedIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String prefIdentity;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        isLoggedIN =prefs.getBoolean("LoggedIn",false);
        prefIdentity = prefs.getString("Identity","default");

        Toast.makeText(this,"Logged in with Identity : " + prefIdentity,Toast.LENGTH_LONG).show();

        isAllFabsVisible = false;
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        fab = findViewById(R.id.menuFab);
        webViewText = findViewById(R.id.webViewText);
        inboxText = findViewById(R.id.inboxText);
        inappText = findViewById(R.id.inappText);
        nativeDisplayText = findViewById(R.id.nativeDisplayText);

        inappFab = findViewById(R.id.inappFab);
        inboxFab = findViewById(R.id.inboxFab);
        webView = findViewById(R.id.webView);
        nativeDisplay = findViewById(R.id.nativeDisplay);

        webViewText.setVisibility(View.GONE);
        inboxText.setVisibility(View.GONE);
        inappText.setVisibility(View.GONE);
        inappFab.setVisibility(View.GONE);
        inboxFab.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        nativeDisplay.setVisibility(View.GONE);
        nativeDisplayText.setVisibility(View.GONE);

        fab.setOnClickListener(view -> {

            if (!isAllFabsVisible){
                webViewText.setVisibility(View.VISIBLE);
                inboxText.setVisibility(View.VISIBLE);
                inappText.setVisibility(View.VISIBLE);
                nativeDisplayText.setVisibility(View.VISIBLE);
                inappFab.show();
                inboxFab.show();
                webView.show();
                nativeDisplay.show();

                isAllFabsVisible = true;
                Snackbar.make(view, "Visible", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
            else{
                webViewText.setVisibility(View.GONE);
                inboxText.setVisibility(View.GONE);
                inappText.setVisibility(View.GONE);
                nativeDisplayText.setVisibility(View.GONE);
                inappFab.hide();
                inboxFab.hide();
                webView.hide();
                nativeDisplay.hide();

                isAllFabsVisible = false;
                Snackbar.make(view, "Hidden", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

//
        webView.setOnClickListener(view -> {
            Intent di = new Intent(getApplicationContext(),webview.class);
            startActivity(di);
        });

        nativeDisplay.setOnClickListener(view -> {

            clevertapDefaultInstance.pushEvent("Native Event");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent di = new Intent(getApplicationContext(),displayNative.class);
            startActivity(di);
        });
    }



}