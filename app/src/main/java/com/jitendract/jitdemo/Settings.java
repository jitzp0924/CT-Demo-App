package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;

public class Settings extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_CUSTOM_INBOX_ENABLED = "custom_inbox_enabled";
    private static final String KEY_TEXT1 = "fastagId";
    private static final String KEY_TEXT2 = "phoneNo";
    private static final String KEY_TEXT3 = "electricId";
    private static final String KEY_TEXT4 = "gasId";
    private static final String KEY_TEXT5= "dthId";
    private static final String KEY_TEXT6= "broadbandId";

    private SharedPreferences sharedPreferences;
    private Switch customInboxSwitch;
    private EditText fastagId,phoneNo,electricId,gasId,dthId,broadbandId;

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
        fastagId = findViewById(R.id.fastagid);
        phoneNo = findViewById(R.id.phoneNo);
        electricId = findViewById(R.id.electricityConsumerNumber);
        gasId = findViewById(R.id.pipedGasConsumerNumber);
        dthId = findViewById(R.id.dthConsumerNumber);
        broadbandId = findViewById(R.id.broadbandId);

        // Set initial state of switch based on SharedPreferences
        boolean switchState = sharedPreferences.getBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
        customInboxSwitch.setChecked(switchState);

        // Load text preferences from SharedPreferences
        fastagId.setText(sharedPreferences.getString(KEY_TEXT1, ""));
        phoneNo.setText(sharedPreferences.getString(KEY_TEXT2, ""));
        electricId.setText(sharedPreferences.getString(KEY_TEXT3, ""));
        gasId.setText(sharedPreferences.getString(KEY_TEXT4, ""));
        dthId.setText(sharedPreferences.getString(KEY_TEXT5, ""));
        broadbandId.setText(sharedPreferences.getString(KEY_TEXT6, ""));

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

        // Set listeners for text edits to update SharedPreferences
        fastagId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT1, s.toString()).apply();
            }
        });

        phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT2, s.toString()).apply();
            }
        });

        electricId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT3, s.toString()).apply();
            }
        });

        gasId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT4, s.toString()).apply();
            }
        });

        dthId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT5, s.toString()).apply();
            }
        });

        broadbandId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(KEY_TEXT6, s.toString()).apply();
            }
        });
    }

    private boolean doesSharedPreferencesExist(String sharedPreferencesName) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getAll().size() > 0; // Check if there are any existing preferences
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
        editor.putString(KEY_TEXT1, "");
        editor.putString(KEY_TEXT2, "");
        editor.putString(KEY_TEXT3, "");
        editor.putString(KEY_TEXT4, "");
        editor.putString(KEY_TEXT5, "");
        editor.putString(KEY_TEXT6, "");
        editor.apply();

        Toast.makeText(this, "Preferences created with default values", Toast.LENGTH_SHORT).show();
    }
}
