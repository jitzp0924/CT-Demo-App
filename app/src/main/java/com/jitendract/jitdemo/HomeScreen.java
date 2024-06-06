package com.jitendract.jitdemo;

import static com.clevertap.android.geofence.Logger.VERBOSE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.variables.callbacks.FetchVariablesCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class HomeScreen extends AppCompatActivity implements CTInboxListener, DisplayUnitListener {

    FloatingActionButton fab;
    private FirebaseAnalytics mFirebaseAnalytics;
    Boolean isAllFabsVisible;
    TextView webViewText,inboxText,inappText,nativeDisplayText;
    FloatingActionButton inappFab,inboxFab,webView,nativeDisplay;
    Button controls;
    boolean isLoggedIN;
    String url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png";
    String url2 = "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp";
    String url3 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

    CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());

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
//        Multi Instance
        CleverTapInstanceConfig clevertapDefaultInstance2 =  CleverTapInstanceConfig.createInstance(this, "65R-654-5Z6Z", "456-256");
        clevertapDefaultInstance2.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
//        CleverTapAPI.getDefaultInstance(this).suspendInAppNotifications();
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);
        clevertapDefaultInstance.fetchVariables(new FetchVariablesCallback() {
            @Override
            public void onVariablesFetched(boolean isSuccess) {
                // isSuccess is true when server request is successful, false otherwise
                Log.e("First Image Variable", (String) clevertapDefaultInstance.getVariableValue("img_url1"));
                Log.e("Second Image Variable", (String) clevertapDefaultInstance.getVariableValue("img_url2"));

            }
        });

        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(VERBOSE)//Log Level
                .setGeofenceMonitoringCount(20)//int value for number of Geofences CleverTap can monitor
                .build();

        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings,clevertapDefaultInstance);
        try {
            CTGeofenceAPI.getInstance(getApplicationContext()).triggerLocation();
        } catch (IllegalStateException e){
            // thrown when this method is called before geofence SDK initialization
        }

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

        controls = findViewById(R.id.settingControl);

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
            nt.put("trip_finish",500);
            clevertapDefaultInstance.pushEvent("Native Event",nt);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent di = new Intent(getApplicationContext(),displayNative.class);
            startActivity(di);
        });

        inappFab.setOnClickListener(View ->{
            Intent inapp=new Intent(getApplicationContext(),temp.class);
            startActivity(inapp);
        });

        controls.setOnClickListener(View ->{
            clevertapDefaultInstance.setOptOut(false);
            Intent di = new Intent(getApplicationContext(), ControlCenter.class);
            startActivity(di);
        });


        HashMap<String, Object> homeScreen = new HashMap<String, Object>();
        homeScreen.put("Date",new Date());

        clevertapDefaultInstance.pushEvent("Home Screen",homeScreen);

        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }
//
//        ArrayList arrayList = CleverTapAPI.getDefaultInstance(this).getAllDisplayUnits();
//        Log.e("getAllDisplayUnits Home", String.valueOf(arrayList));

        sliderInit(clevertapDefaultInstance);



    }


    public void logoutClick(View view) {

        SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
        editor.remove("LoggedIn").apply();
        editor.remove("Identity").apply();

        Intent di = new Intent(getApplicationContext(),MainActivity.class);
        di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(di);
    }


//    @Override
//    public void inboxDidInitialize() {
//
//        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        inboxFab.setOnClickListener(v -> {
//
//            HashMap<String, Object> AInbox = new HashMap<String, Object>();
//            AInbox.put("Date",new Date());
//            AInbox.put("Screen","HomeScreen FAB");
//            AInbox.put("ChargedID","ET000365F");
//            AInbox.put("TransactionID","3794691403");
//            AInbox.put("Test","Test");
//
//            clevertapDefaultInstance.pushEvent("App-Inbox Event",AInbox);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//            ArrayList<String> tabs = new ArrayList<>();
//            tabs.add("Promotions");
//            tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored
//
//            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
//            styleConfig.setFirstTabTitle("All");
//            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
//            styleConfig.setTabBackgroundColor("#FF0000");
//            styleConfig.setSelectedTabIndicatorColor("#0000FF");
//            styleConfig.setSelectedTabColor("#0000FF");
//            styleConfig.setUnselectedTabColor("#FFFFFF");
//            styleConfig.setBackButtonColor("#FF0000");
//            styleConfig.setNavBarTitleColor("#FF0000");
//            styleConfig.setNavBarTitle("MY INBOX");
//            styleConfig.setNavBarColor("#FFFFFF");
//            styleConfig.setInboxBackgroundColor("#ADD8E6");
//            if (clevertapDefaultInstance != null) {
//                clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
//            }
//            //ct.showAppInbox();//Opens Activity with default style configs
//        });
//    }

    @Override
    public void inboxDidInitialize() {
        Log.e("App Inbox", "inboxDidInitialize");
    }

    @Override
    public void inboxMessagesDidUpdate() {
        Log.e("App Inbox", "inboxMessagesDidUpdate");
        ArrayList arrayList = CleverTapAPI.getDefaultInstance(this).getAllInboxMessages();
        Log.e("App Inbox", String.valueOf(arrayList));


    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        for (int i = 0; i <units.size() ; i++) {
            CleverTapDisplayUnit unit = units.get(i);
            Log.e("Home Screen UNit", String.valueOf(unit));
            Objects.requireNonNull(CleverTapAPI.getDefaultInstance(this)).pushDisplayUnitViewedEventForID(unit.getUnitID());
        }
    }

    private void sliderInit(CleverTapAPI clevertapDefaultInstance) {
        // we are creating array list for storing our image urls.
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        url1 = clevertapDefaultInstance.getVariableValue("img_url1").toString();
        url2 = clevertapDefaultInstance.getVariableValue("img_url2").toString();
        url3 = clevertapDefaultInstance.getVariableValue("img_url3").toString();
        // adding the urls inside array list
        sliderDataArrayList.add(new SliderData(url1));
        sliderDataArrayList.add(new SliderData(url2));
        sliderDataArrayList.add(new SliderData(url3));

        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(3);

        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            cleverTapAPI.pushNotificationClickedEvent(intent.getExtras());
        }
    }
}