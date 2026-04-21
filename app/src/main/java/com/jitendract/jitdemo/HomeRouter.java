package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import com.clevertap.android.sdk.CleverTapAPI;

public final class HomeRouter {

    private HomeRouter() {}

    /**
     * Returns an Intent for the correct homescreen based on the "newHomeEnabled" PE flag.
     * Falls back to HomeScreen2 if the flag is absent or false.
     */
    public static Intent getHomeIntent(Context context) {
        boolean useV2 = false;
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(context);
        if (ct != null) {
            Object flag = ct.getVariableValue("newHomeEnabled");
            if (flag instanceof Boolean) useV2 = (Boolean) flag;
        }
        return useV2
                ? new Intent(context, HomeScreenV2.class)
                : new Intent(context, HomeScreen2.class);
    }
}
