package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.clevertap.android.sdk.CleverTapAPI;

public class MainActivity extends AppCompatActivity {

    EditText accId,accToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accId = findViewById(R.id.accId);
        accToken = findViewById(R.id.accToken);
    }

    public void accountCred(View view) {

        Intent di = new Intent(getApplicationContext(),HomeScreen.class);
        startActivity(di);

    }

    public void defCred(View view) {

        Intent di = new Intent(getApplicationContext(),Anonymous.class);
        startActivity(di);
    }
}