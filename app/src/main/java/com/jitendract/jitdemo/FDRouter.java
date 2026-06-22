package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.clevertap.android.sdk.CleverTapAPI;
import java.util.Map;

public final class FDRouter {

    private FDRouter() {}

    /**
     * Returns an Intent for the correct FD journey based on the FDJourney PE variable.
     * FDJourney is a top-level Map variable; version is read from FDJourney.version.
     * Expected values: "V1" (default) → single-screen booking, "V2" → multi-step flow.
     * Falls back to V1 if the variable is absent or unrecognised.
     */
    public static Intent getFDIntent(Context context) {
        String version = readVersion(context);
        Log.d("FDRouter", "FDJourney.version → " + version);
        switch (version) {
            case "V2": return new Intent(context, FDHome.class);
            case "V1":
            default:   return new Intent(context, FDBookingActivity.class);
        }
    }

    /** Reads FDJourney.version from the top-level FDJourney PE Map. */
    static String readVersion(Context context) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(context);
        if (ct == null) return "V1";
        try {
            Map<String, Object> fdJourney = (Map<String, Object>) ct.getVariableValue("FDJourney");
            if (fdJourney != null) {
                Object v = fdJourney.get("version");
                if (v instanceof String) return ((String) v).trim().toUpperCase();
            }
        } catch (Exception e) {
            Log.e("FDRouter", "Failed to read FDJourney variable", e);
        }
        return "V1";
    }
}
