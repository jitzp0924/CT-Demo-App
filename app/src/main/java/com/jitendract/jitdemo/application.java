package com.jitendract.jitdemo;

import android.app.Application;
import com.clevertap.android.sdk.ActivityLifecycleCallback;

public class application extends Application {

    @Override
    public void onCreate() {

        ActivityLifecycleCallback.register(this);


        super.onCreate();
    }
}
