package com.jitendract.jitdemo;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.rgb;
import static java.lang.Boolean.TRUE;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.PushPermissionResponseListener;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CTPushNotificationListener {

    TextInputLayout idLayout,phoneLayout;
    MaterialCardView loginbtn;
    CheckBox conditions,commsUpdate;
    TextInputEditText identity,phone;
    CleverTapAPI clevertapDefaultInstance;

    String userID, phoneNum,topBannerUrl,appType;
    Boolean topBannerStatus, locationPermissionGranted;
    Map <String, Object>LoginSceen;
    ImageView topBanner;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    TextView signInText,loginBtnText,mainText;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idLayout = findViewById(R.id.userTextL);
        phoneLayout = findViewById(R.id.numTextL);

        signInText = findViewById(R.id.signInText);
        loginBtnText = findViewById(R.id.loginBtnText);
        mainText = findViewById(R.id.mainText);

        identity = findViewById(R.id.identity);
        phone = findViewById(R.id.phone);
        loginbtn = findViewById(R.id.loginbtn);
        conditions = findViewById(R.id.conditions);
        commsUpdate = findViewById(R.id.commsUpdate);
        topBanner = findViewById(R.id.loginTopBanner);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        CleverTapUtils cleverTapUtils = CleverTapUtils.getInstance();

        clevertapDefaultInstance.fetchVariables();

        appType = String.valueOf(clevertapDefaultInstance.getVariableValue("appType"));
        LoginSceen = (Map<String, Object>) clevertapDefaultInstance.getVariableValue("LoginScreen");
        topBannerStatus = (Boolean) LoginSceen.get("Active");
        topBannerUrl = (String) LoginSceen.get("Top BannerImage");

        signInText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,SignInPage.class);
            startActivity(intent);
        });


        if (TRUE.equals(topBannerStatus)){
            try {
                topBanner.setVisibility(View.VISIBLE);

                Glide.with(this).load(topBannerUrl).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(topBanner);
//                topBanner.setImageURI(Uri.parse(topBannerUrl));
            }
            catch (Exception e){
                Log.e("PE_Exception",String.valueOf(e));
            }

        }else {topBanner.setVisibility(View.GONE);}

        if (appType.equals("rapido")){
            loginbtn.setCardBackgroundColor(Color.parseColor("#f9c935"));
            loginBtnText.setTextColor(rgb(255,255,255));
            mainText.setTextColor(Color.parseColor("#f9c935"));

        }
        else {
            loginbtn.setCardBackgroundColor(Color.parseColor("#ad5154"));
            loginBtnText.setTextColor(rgb(0,0,0));
            mainText.setTextColor(rgb(255,255,255));
            topBanner.setVisibility(View.GONE);
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
                if(userID.contains("rapido")){loginMap.put("AppType","rapido");}else loginMap.put("AppType","basic");
                loginMap.put("Phone",phoneNum);
                loginMap.put("MSG-whatsapp",commsUpdate.isChecked());
                loginMap.put("T&C",conditions.isChecked());
                cleverTapUtils.login(loginMap);

                HashMap<String, Object> evtMAp = new HashMap<>();
                evtMAp.put("Screen","Login");      // String or number
                evtMAp.put("Status","Success");
                evtMAp.put("ID-",userID);
                cleverTapUtils.raiseEvent("Logged In",evtMAp);
                SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                editor.putBoolean("LoggedIn",true);
                editor.putString("Identity",userID);
                editor.putString("Phone",phoneNum);
                editor.putBoolean("locationPermissionGranted",locationPermissionGranted);
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
                                locationPermissionGranted =TRUE;


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



        if (Build.VERSION.SDK_INT >= 32){
            clevertapDefaultInstance.setCTPushNotificationListener(this);

        }
        else{
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2", NotificationManager.IMPORTANCE_MAX,true);
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"jiosound","jiosound","For Jio", NotificationManager.IMPORTANCE_MAX,true,"jiosound.mp3");
        }

    }



//    @Override
//    public void onPushPermissionResponse(boolean accepted) {
//        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        Log.i(TAG, "onPushPermissionResponse :  InApp---> response() called accepted="+accepted);
//        if(accepted){
//            //For Android 13+ we need to create notification channel after notification permission is accepted
//            CleverTapAPI.createNotificationChannel(getApplicationContext(),"JitDemo","JitDemo","JitDemo", NotificationManager.IMPORTANCE_MAX,true);
//            CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2 sound bad", NotificationManager.IMPORTANCE_MAX,true);
//            CleverTapAPI.createNotificationChannel(getApplicationContext(),"jiosound","jiosound","For JIO", NotificationManager.IMPORTANCE_MAX,true,"jiosound.mp3");
//            clevertapDefaultInstance.pushEvent("Push Opted-in");
//        }
//        else{
//            clevertapDefaultInstance.pushEvent("Push Opted-out");
//
//        }
//    }



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
