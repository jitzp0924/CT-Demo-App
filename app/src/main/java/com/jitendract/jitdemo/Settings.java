package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;

public class Settings extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_CUSTOM_INBOX_ENABLED = "custom_inbox_enabled";

    private SharedPreferences sharedPreferences;
    private Switch customInboxSwitch;

    CleverTapAPI cleverTapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        boolean sharedPreferencesExists = doesSharedPreferencesExist(PREF_NAME);

        if (!sharedPreferencesExists) {
            // Initialize SharedPreferences with default values
            initializeSharedPreferences();
        }

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        customInboxSwitch = findViewById(R.id.custom_inbox_switch);

        // Set initial state of switch based on SharedPreferences
        boolean switchState = sharedPreferences.getBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
        customInboxSwitch.setChecked(switchState);

        // Set listener for switch change
        customInboxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_CUSTOM_INBOX_ENABLED, isChecked);
                editor.apply();

                if (isChecked) {
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Custom Inbox", "Yes");
                    cleverTapDefaultInstance.pushProfile(profileUpdate);
                } else {
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Custom Inbox", "No");
                    cleverTapDefaultInstance.pushProfile(profileUpdate);
                }
            }
        });
    }
    private boolean doesSharedPreferencesExist(String sharedPreferencesName) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getAll().size() > 0; // Check if there are any existing preferences
    }
    private void initializeSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
        editor.apply();

        Toast.makeText(this, "Preference created with default value", Toast.LENGTH_SHORT).show();
    }
}
