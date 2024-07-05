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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.PushPermissionResponseListener;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.pushnotification.PushConstants;
import com.clevertap.android.sdk.variables.callbacks.FetchVariablesCallback;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jitendract.jitdemo.CleveTapUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CTPushNotificationListener, PushPermissionResponseListener {

    TextInputLayout idLayout,phoneLayout;
    MaterialCardView loginbtn;
    CheckBox conditions,commsUpdate;
    TextInputEditText identity,phone;
    CleverTapAPI clevertapDefaultInstance;

    String userID, phoneNum,topBannerUrl;
    Boolean topBannerStatus;
    Map <String, Object>LoginSceen;
    ImageView topBanner;

    TextView signInText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idLayout = (TextInputLayout) findViewById(R.id.userTextL);
        phoneLayout = (TextInputLayout) findViewById(R.id.numTextL);

        signInText = findViewById(R.id.signInText);

        identity = (TextInputEditText) findViewById(R.id.identity);
        phone = (TextInputEditText) findViewById(R.id.phone);
        loginbtn = (MaterialCardView) findViewById(R.id.loginbtn);
        conditions = (CheckBox) findViewById(R.id.conditions);
        commsUpdate = (CheckBox) findViewById(R.id.commsUpdate);
        topBanner = (ImageView) findViewById(R.id.loginTopBanner);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        CleveTapUtils cleveTapUtils = new CleveTapUtils(getApplicationContext());

        clevertapDefaultInstance.fetchVariables();

        signInText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,SignInPage.class);
            startActivity(intent);
        });

        LoginSceen = (Map<String, Object>) clevertapDefaultInstance.getVariableValue("LoginScreen");
        topBannerStatus = (Boolean) LoginSceen.get("Active");
        topBannerUrl = (String) LoginSceen.get("Top BannerImage");

        if (Boolean.TRUE.equals(topBannerStatus)){
            try {
                Glide.with(this).load(topBannerUrl).placeholder(R.drawable.login_banner).fitCenter().into(topBanner);
//                topBanner.setImageURI(Uri.parse(topBannerUrl));
            }
            catch (Exception e){
                Log.e("PE_Exception",String.valueOf(e));
            }

        }

        identity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userID = String.valueOf(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()<10 || editable.length()>10){
                    phoneLayout.setError("Phone number must be 10 digits");
                }
                if (editable.length()==10){
                    phoneLayout.setErrorEnabled(false);
                    phoneLayout.setError(null);
                    phoneNum = String.valueOf(editable);
                }
            }
        });

        loginbtn.setOnClickListener(v ->{

            if (userID==null || phoneNum==null || !conditions.isChecked()){

                if (userID==null){
                    idLayout.setErrorEnabled(true);
                    idLayout.setError("Please enter UserId");
                }
                if (phoneNum==null){
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError("Phone number must be 10 digits");
                }
                if (!conditions.isChecked()){
                    Toast.makeText(this,"Please Agree to Terms & Conditions",Toast.LENGTH_LONG).show();
                }
            }
            else {

                HashMap<String, Object> loginMap = new HashMap<>();
                loginMap.put("Identity",userID);      // String or number
                loginMap.put("Phone",phoneNum);
                loginMap.put("MSG-whatsapp",commsUpdate.isChecked());
                loginMap.put("T&C",conditions.isChecked());
                cleveTapUtils.login(loginMap);

                HashMap<String, Object> evtMAp = new HashMap<>();
                evtMAp.put("Screen","Login");      // String or number
                evtMAp.put("Status","Success");
                evtMAp.put("ID-",userID);
                cleveTapUtils.raiseEvent("Logged In",evtMAp);
                SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                editor.putBoolean("LoggedIn",true);
                editor.putString("Identity",userID);
                editor.putString("Phone",phoneNum);
                editor.apply();

                Intent di = new Intent(getApplicationContext(),HomeScreen2.class);
                di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(di);

            }
        });


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



    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Toast.makeText(this, (CharSequence) payload,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            clevertapDefaultInstance.pushNotificationClickedEvent(intent.getExtras());
        }
    }
}
