package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;

public class Anonymous extends AppCompatActivity {

    EditText EvtName,EvtPropName,EvtPropVal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous);

        EvtName = findViewById(R.id.eventName);
        EvtPropName = findViewById(R.id.propName);
        EvtPropVal = findViewById(R.id.propVal);
    }


    public void evtPush(View view) {

        String evtName = EvtName.getText().toString();
        String evtPropName = EvtPropName.getText().toString();
        String evtPropVal = EvtPropVal.getText().toString();
        Context context = getApplicationContext();


        HashMap<String, Object> evtProps = new HashMap<String, Object>();
        evtProps.put(evtPropName, evtPropVal);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
        clevertapDefaultInstance.pushEvent(evtName,evtProps);

        Toast toast = Toast.makeText(context,evtName + "Event pushed with property - "+evtPropName+" : "+evtPropVal, Toast.LENGTH_LONG);
        toast.show();


    }
}