package com.jitendract.jitdemo;

import static android.content.ContentValues.TAG;

import static com.clevertap.android.geofence.Logger.VERBOSE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.PushPermissionResponseListener;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.pushnotification.PushConstants;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CTPushNotificationListener, PushPermissionResponseListener {

    EditText identity,email,phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CleverTapAPI.enableXiaomiPushOn(PushConstants.ALL_DEVICES);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        //        Multi Instance
        CleverTapInstanceConfig clevertapDefaultInstance2 =  CleverTapInstanceConfig.createInstance(this, "65R-654-5Z6Z", "456-256");

        // Location Permission Base Android
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(VERBOSE)//Log Level
                .setGeofenceMonitoringCount(20)//int value for number of Geofences CleverTap can monitor
                .build();

        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings,clevertapDefaultInstance);

        identity = findViewById(R.id.identity);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        if (Build.VERSION.SDK_INT >= 32){
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.registerPushPermissionNotificationResponseListener(this);
            }
            clevertapDefaultInstance.setCTPushNotificationListener(this);
//
//            @SuppressLint("RestrictedApi") JSONObject jsonObject = CTLocalInApp.builder()
//                    .setInAppType(CTLocalInApp.InAppType.HALF_INTERSTITIAL)
//                    .setTitleText("Get Latest Offers & Promotions")
//                    .setMessageText("Click on allow to get the latest updates from us !!!!!")
//                    .followDeviceOrientation(true)
//                    .setPositiveBtnText("Let's Do THis!")
//                    .setNegativeBtnText("Maybe Later.")
//                    .setBackgroundColor(Constants.WHITE)
//                    .setBtnBorderColor("#FFD700")
//                    .setTitleTextColor(Constants.WHITE)
//                    .setMessageTextColor(Constants.BLACK)
//                    .setBtnTextColor(Constants.WHITE)
//                    .setImageUrl("https://media-exp1.licdn.com/dms/image/C5622AQFo1izBws7Lhw/feedshare-shrink_2048_1536/0/1658384964241?e=2147483647&v=beta&t=lkJWbHD_8n5v27GtOg4gynsRu_PunE1Z33XY0jJetDQ")
//                    .setBtnBackgroundColor(Constants.BLACK)
//                    .build();
////                .setInAppType(CTLocalInApp.InAppType.ALERT)
////                .setTitleText("Get Notified")
////                .setMessageText("Enable Notification permission")
////                .followDeviceOrientation(true)
////                .setPositiveBtnText("Allow")
////                .setNegativeBtnText("Cancel")
////                .build();
//            clevertapDefaultInstance.promptPushPrimer(jsonObject);


        }
        else{
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2 sound bad", NotificationManager.IMPORTANCE_MAX,true,"r2d2.mp3");
        }

        clevertapDefaultInstance.promptForPushPermission(true);


    }



    @Override
    public void onPushPermissionResponse(boolean accepted) {
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        Log.i(TAG, "onPushPermissionResponse :  InApp---> response() called accepted="+accepted);
        if(accepted){
            //For Android 13+ we need to create notification channel after notification permission is accepted
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"JitDemo","JitDemo","JitDemo", NotificationManager.IMPORTANCE_MAX,true);
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2 sound bad", NotificationManager.IMPORTANCE_MAX,true,"r2d2.mp3");
            clevertapDefaultInstance.pushEvent("Push Opted-in");
        }
        else{
            clevertapDefaultInstance.pushEvent("Push Opted-out");

        }
    }


    public void loginClick(View view) {

        String Email = email.getText().toString();
        String Identity = identity.getText().toString();
        String Phone = phone.getText().toString();

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapInstanceConfig clevertapDefaultInstance2 =  CleverTapInstanceConfig.createInstance(this, "65R-654-5Z6Z", "456-256");
        CleverTapAPI cleverTapAPI = CleverTapAPI.instanceWithConfig(this,clevertapDefaultInstance2);

        HashMap<String, Object> profileUpdate = new HashMap<>();
        profileUpdate.put("Identity",Identity);      // String or number
        profileUpdate.put("Email",Email);
        profileUpdate.put("Phone",Phone);
        profileUpdate.put("MSG-push",true);
        clevertapDefaultInstance.onUserLogin(profileUpdate);
        cleverTapAPI.onUserLogin(profileUpdate);

        SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
        editor.putBoolean("LoggedIn",true);
        editor.putString("Identity",Identity);
        editor.apply();

        Intent di = new Intent(getApplicationContext(),HomeScreen.class);
        di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(di);




    }

    public void defPage(View view) {

        Intent di = new Intent(getApplicationContext(),Anonymous.class);
        startActivity(di);
    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Toast.makeText(this, (CharSequence) payload,Toast.LENGTH_LONG).show();
    }

}