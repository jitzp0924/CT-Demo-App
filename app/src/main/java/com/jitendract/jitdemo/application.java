package com.jitendract.jitdemo;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;

public class application extends Application {

    @Override
    public void onCreate() {
        SharedPreferences sp = getSharedPreferences("CTCreds",MODE_PRIVATE);
        String AccId = sp.getString("AccountId","");
        String AccToken = sp.getString("AccountToken","");

        Log.d("Credentials JT","CleverTap Credentials : "+AccId+"    "+AccToken);
        CleverTapAPI.changeCredentials(AccId, AccToken);

        ActivityLifecycleCallback.register(this);


        super.onCreate();
    }
}
