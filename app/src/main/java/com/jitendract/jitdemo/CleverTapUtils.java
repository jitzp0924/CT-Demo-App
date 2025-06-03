package com.jitendract.jitdemo;

import android.content.Context;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

import java.util.HashMap;

public class CleverTapUtils {

    private static CleverTapUtils instance;
    private CleverTapAPI clevertapDefaultInstance;

    // Private constructor
    private CleverTapUtils(Context applicationContext) {
        // Initialize CleverTap default instance
        this.clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext);
    }

    // Public static method to initialize the singleton instance
    public static synchronized void init(Context applicationContext) {
        if (instance == null) {
            instance = new CleverTapUtils(applicationContext.getApplicationContext());
        }
    }

    // Method to get the singleton instance
    public static CleverTapUtils getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CleverTapUtils is not initialized. Call init() first from your Application class.");
        }
        return instance;
    }

    public CleverTapAPI getDefaultInstance() {
        return clevertapDefaultInstance;
    }

    // Method to log in user
    public void login(HashMap<String, Object> loginMap) {
        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.onUserLogin(loginMap);
        }
    }

    // Method to raise event
    public void raiseEvent(String evtName, HashMap<String, Object> evtMap) {
        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.pushEvent(evtName, evtMap);
        }
    }

    // Optional: Method to initialize a multi-instance (if needed)
    public CleverTapAPI createSecondaryInstance(Context context, String accountId, String token) {
        CleverTapInstanceConfig config = CleverTapInstanceConfig.createInstance(context, accountId, token);
        return CleverTapAPI.instanceWithConfig(context, config);
    }
}
