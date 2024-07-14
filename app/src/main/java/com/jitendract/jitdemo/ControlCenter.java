package com.jitendract.jitdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;

public class ControlCenter extends AppCompatActivity {

    RadioButton pn1,pn2,wa1,wa2,sms1,sms2,em1,em2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        CleverTapAPI clevertapInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        pn1 = findViewById(R.id.pn_on);
        pn2 = findViewById(R.id.pn_off);
        wa1 = findViewById(R.id.wa_on);
        wa2 = findViewById(R.id.wa_off);
        sms1 = findViewById(R.id.sms_on);
        sms2 = findViewById(R.id.sms_off);
        em1 = findViewById(R.id.email_on);
        em2 = findViewById(R.id.email_off);

        pn1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-push", true);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });

        pn2.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            if(isChecked) {

                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-push", false);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });

        wa1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-whatsapp", true);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });

        wa2.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-whatsapp", false);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });

        sms1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-sms", true);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });

        sms2.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                profileUpdate.put("MSG-sms", false);
                clevertapInstance.pushProfile(profileUpdate);
            }
        });
    }


}