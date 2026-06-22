package com.jitendract.jitdemo;

import android.content.Context;
import android.util.Log;
import com.clevertap.android.sdk.CleverTapAPI;
import java.util.HashMap;
import java.util.Map;

/**
 * Single point for all "FD Screen" CleverTap events across both FD journey variants.
 *
 * Event name  : FD Screen
 * Properties  : screen_name, fd_journey_version, action,
 *               deposit_amount, tenure_months, rate_of_interest,
 *               nominee_name, maturity_amount
 *
 * Null / unfilled fields are omitted from the payload — in CleverTap this means
 * the property is absent on that event, which analysts can filter as "property does not exist".
 */
public final class FDEventTracker {

    // ── Screen name constants ──────────────────────────────────────────────────
    /** V1 single-screen booking. */
    public static final String SCREEN_BOOKING = "FD Booking";
    /** V2 details entry (FDHome). */
    public static final String SCREEN_DETAILS = "FD Details";
    /** V2 review screen (FDSummary). */
    public static final String SCREEN_REVIEW  = "FD Review";
    /** Shared MPIN screen. */
    public static final String SCREEN_MPIN    = "FD MPIN";
    /** Shared success screen. */
    public static final String SCREEN_SUCCESS = "FD Success";

    // ── Action constants ──────────────────────────────────────────────────────
    public static final String ACTION_VIEW    = "view";
    public static final String ACTION_CLICK   = "click";
    public static final String ACTION_BACK    = "back";
    public static final String ACTION_SUCCESS = "success";

    private FDEventTracker() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Raise an "FD Screen" event.
     *
     * @param context  Any context — used to get the CleverTap instance.
     * @param screen   One of the SCREEN_* constants above.
     * @param action   One of the ACTION_* constants above.
     * @param data     FD form data at the time of the event; null means no data yet (e.g. first view).
     */
    public static void track(Context context, String screen, String action, FDEventPayload data) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(context);
        if (ct == null) return;

        HashMap<String, Object> props = new HashMap<>();
        props.put("screen_name",        screen);
        props.put("fd_journey_version", readJourneyVersion(ct));
        props.put("action",             action);

        if (data != null) {
            putIfPresent(props, "deposit_amount",   data.depositAmount);
            putIfPresent(props, "tenure_months",    data.tenure);
            putIfPresent(props, "rate_of_interest", data.rateOfInterest);
            putIfPresent(props, "nominee_name",     data.nomineeName);
            putIfPresent(props, "maturity_amount",  data.maturityAmount);
        }
        // If data == null (e.g. a pure "view" event), the FD detail keys are absent —
        // this is intentional; analysts can distinguish "no data" from "empty string".

        ct.pushEvent("FD Screen", props);
        Log.d("FDEvent", screen + " | " + action + " | " + props);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Only adds the key if the value is non-null (i.e. the field was filled by the user). */
    private static void putIfPresent(HashMap<String, Object> map, String key, String value) {
        if (value != null) map.put(key, value);
    }

    /**
     * Delegates to FDRouter.readVersion() — single source of truth for reading
     * FDJourney.version from the top-level FDJourney PE Map.
     */
    private static String readJourneyVersion(CleverTapAPI ct) {
        // ct is already non-null here; pass the context via ApplicationContext workaround is not
        // needed because FDRouter.readVersion() calls getDefaultInstance() again internally.
        // We call it via context, so we grab context from ct itself isn't possible — instead,
        // duplicate the Map read here to avoid a circular dependency.
        try {
            Map<String, Object> fdJourney = (Map<String, Object>) ct.getVariableValue("FDJourney");
            if (fdJourney != null) {
                Object v = fdJourney.get("version");
                if (v instanceof String) return ((String) v).trim().toUpperCase();
            }
        } catch (Exception e) {
            Log.e("FDEvent", "Could not read FDJourney.version", e);
        }
        return "V1";
    }
}
