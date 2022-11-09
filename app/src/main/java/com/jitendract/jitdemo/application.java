package com.jitendract.jitdemo;

import android.app.Application;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.NotificationHandler;

public class application extends Application {

    @Override
    public void onCreate() {

        ActivityLifecycleCallback.register(this);
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI.setNotificationHandler((NotificationHandler) new PushTemplateNotificationHandler());

        super.onCreate();
    }
}
