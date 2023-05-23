package com.jitendract.jitdemo;

import static com.clevertap.android.geofence.CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity implements CTInboxListener {

    FloatingActionButton fab;
    private FirebaseAnalytics mFirebaseAnalytics;
    Boolean isAllFabsVisible;
    TextView webViewText,inboxText,inappText,nativeDisplayText;
    FloatingActionButton inappFab,inboxFab,webView,nativeDisplay;
    boolean isLoggedIN;

    @SuppressLint("WrongThread")
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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(CleverTapAPI.getDefaultInstance(this)).getCleverTapID());

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

            HashMap<String, Object> nt = new HashMap<String, Object>();
            nt.put("Date",new Date());
            nt.put("Screen","HomeScreen FAB");
            clevertapDefaultInstance.pushEvent("Native Event",nt);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent di = new Intent(getApplicationContext(),displayNative.class);
            startActivity(di);
        });

        inboxFab.setOnClickListener(view ->{
            clevertapDefaultInstance.pushEvent("App-Inbox Event");
//            Intent inbox = new Intent(getApplicationContext(),CustomAppInbox.class);
//            startActivity(inbox);

        });


        HashMap<String, Object> homeScreen = new HashMap<String, Object>();
        homeScreen.put("Date",new Date());

        clevertapDefaultInstance.pushEvent("Home Screen Load",homeScreen);
        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH )//byte value for Location Accuracy
                .setLocationFetchMode(FETCH_CURRENT_LOCATION_PERIODIC )//byte value for Fetch Mode
                .setInterval(180000)
                .build();
        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings,clevertapDefaultInstance);

        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }
    }


    public void logoutClick(View view) {

        SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
        editor.remove("LoggedIn").apply();
        editor.remove("Identity").apply();

        Intent di = new Intent(getApplicationContext(),MainActivity.class);
        di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(di);
    }

    @Override
    public void inboxDidInitialize() {

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        inboxFab.setOnClickListener(v -> {

            clevertapDefaultInstance.pushEvent("App-Inbox Event");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("Promotions");
            tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored

            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
            styleConfig.setTabBackgroundColor("#FF0000");
            styleConfig.setSelectedTabIndicatorColor("#0000FF");
            styleConfig.setSelectedTabColor("#0000FF");
            styleConfig.setUnselectedTabColor("#FFFFFF");
            styleConfig.setBackButtonColor("#FF0000");
            styleConfig.setNavBarTitleColor("#FF0000");
            styleConfig.setNavBarTitle("MY INBOX");
            styleConfig.setNavBarColor("#FFFFFF");
            styleConfig.setInboxBackgroundColor("#ADD8E6");
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            }
            //ct.showAppInbox();//Opens Activity with default style configs
        });
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }
}