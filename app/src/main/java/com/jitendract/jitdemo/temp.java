package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clevertap.android.sdk.CleverTapAPI;

public class temp extends AppCompatActivity {

    public Button btn, btn2, btn3 , btn4, btn5, btn6,btn7,btn8,btn9,btn11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        Button btn = (Button) findViewById(R.id.basictem);
        Button btn2 = (Button) findViewById(R.id.bt2);
        Button btn3 = (Button) findViewById(R.id.bt3);
        Button btn4 = (Button) findViewById(R.id.bt4);
        Button btn6 = (Button) findViewById(R.id.bt6);
        Button btn7 = (Button) findViewById(R.id.bt7);
        Button btn8=(Button) findViewById(R.id.bt8);
        Button btn9=(Button) findViewById(R.id.bt9);

        btn.findViewById(R.id.basictem).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Basic Template");
        });
        btn2.findViewById(R.id.bt2).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Auto Template");
        });
        btn3.findViewById(R.id.bt3).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Manual c Template");
        });
        btn4.findViewById(R.id.bt4).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Rating Push");
        });
        btn6.findViewById(R.id.bt6).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Input box template");
        });
        btn7.findViewById(R.id.bt7).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Product Catalog");
        });
        btn9.findViewById(R.id.bt9).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Five Icons");
        });
        btn8.findViewById(R.id.bt8).setOnClickListener(view -> {
            clevertapDefaultInstance.pushEvent("Timer Template");
        });

    }
}