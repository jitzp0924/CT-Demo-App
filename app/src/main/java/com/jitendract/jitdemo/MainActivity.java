package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.pushnotification.PushConstants;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CTPushNotificationListener {

    EditText identity,email,phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CleverTapAPI.enableXiaomiPushOn(PushConstants.ALL_DEVICES);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        clevertapDefaultInstance.setCTPushNotificationListener(this);
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"JitDemo","JitDemo","JitDemo", NotificationManager.IMPORTANCE_MAX,true);
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2 sound bad", NotificationManager.IMPORTANCE_MAX,true,"r2d2.mp3");


        identity = findViewById(R.id.identity);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);


    }


    public void loginClick(View view) {

        String Email = email.getText().toString();
        String Identity = identity.getText().toString();
        String Phone = phone.getText().toString();

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        HashMap<String, Object> profileUpdate = new HashMap<>();
        profileUpdate.put("Identity",Identity);      // String or number
//        profileUpdate.put("Email",Email);
        profileUpdate.put("Phone",Phone);
        clevertapDefaultInstance.onUserLogin(profileUpdate);

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