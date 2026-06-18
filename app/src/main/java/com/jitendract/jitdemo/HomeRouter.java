package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.Map;

public final class HomeRouter {

    private HomeRouter() {}

    /**
     * Returns an Intent for the correct homescreen based on the "newHomeEnabled" PE variable.
     * Expected values: "V1" (default), "V2", "V3" (future).
     * Falls back to HomeScreen2 (V1) if the variable is absent or unrecognised.
     */
    public static Intent getHomeIntent(Context context) {
        String version = "V1";
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(context);
        if (ct != null) {
            try {
                Map<String, Object> homeScreen = (Map<String, Object>) ct.getVariableValue("HomeScreen");
                if (homeScreen != null) {
                    Object flag = homeScreen.get("newHomeEnabled");
                    Log.d("HomeRouter", "newHomeEnabled: " + flag);
                    if (flag instanceof String) version = ((String) flag).trim().toUpperCase();
                }
            } catch (Exception e) {
                Log.e("HomeRouter", "Failed to read HomeScreen variable", e);
            }
        }
        switch (version) {
            case "V2":
                return new Intent(context, HomeScreenV2.class);
            // V3 and beyond: add new cases here as new screens are introduced
            case "V1":
            default:
                return new Intent(context, HomeScreen2.class);
        }
    }
}
