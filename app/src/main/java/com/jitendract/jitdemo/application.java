package com.jitendract.jitdemo;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.pushtemplates.PTConstants;
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.InAppNotificationButtonListener;
import com.clevertap.android.sdk.interfaces.OnInitCleverTapIDListener;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jitendract.jitdemo.CtPE.PEVariables;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import static com.clevertap.android.geofence.CTGeofenceSettings.ACCURACY_HIGH;
import static com.clevertap.android.geofence.CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC;

public class application extends Application implements Application.ActivityLifecycleCallbacks,
        CTPushNotificationListener,
        InAppNotificationButtonListener {

    private static final String TAG_INTENT = "IntentDebug";
    private static final String TAG_LIFECYCLE = "AppLifecycle";

    public static AppDatabase database;

    private CleverTapAPI cleverTapAPI;
    private FirebaseAnalytics firebaseAnalytics;

    /**
     * Counts activities that are Started but not yet Stopped.
     * Navigating A→B: B.onStart fires before A.onStop → count goes 1→2→1, never 0.
     * Pressing Home:  A.onStop fires with nothing starting → count drops to 0 → true background.
     */
    private int activeActivityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase
        FirebaseApp.initializeApp(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Lifecycle
        registerActivityLifecycleCallbacks(this);

        // CleverTap
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        cleverTapAPI = CleverTapAPI.getDefaultInstance(this);

        if (cleverTapAPI != null) {
            initCleverTap(cleverTapAPI);
            initProductExperiences(cleverTapAPI);
            initGeofence(cleverTapAPI);
        }



        // DB
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "transactions-db").build();
    }

    /* ---------------------------------- */
    /* Activity Lifecycle                  */
    /* ---------------------------------- */

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.v(TAG_LIFECYCLE, "Created: " + activity.getClass().getSimpleName());
        logIntent(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activeActivityCount++;
        Log.v(TAG_LIFECYCLE, "Started: " + activity.getClass().getSimpleName()
                + " (active=" + activeActivityCount + ")");
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.v(TAG_LIFECYCLE, "Resumed: " + activity.getClass().getSimpleName());
        handlePushDismiss(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.v(TAG_LIFECYCLE, "Paused: " + activity.getClass().getSimpleName());
        // Do NOT raise background here — onPaused fires on every screen transition within the app.
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activeActivityCount--;
        Log.v(TAG_LIFECYCLE, "Stopped: " + activity.getClass().getSimpleName()
                + " (active=" + activeActivityCount + ")");
        if (activeActivityCount == 0) {
            // All activities are stopped — user pressed Home or switched apps.
            trackAppBackground();
        }
    }

    @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // Destroyed fires during normal back-navigation too — not a background signal.
        Log.v(TAG_LIFECYCLE, "Destroyed: " + activity.getClass().getSimpleName());
    }

    /* ---------------------------------- */
    /* Intent Logging                      */
    /* ---------------------------------- */

    private void logIntent(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) return;

        Log.d(TAG_INTENT, "---- " + activity.getClass().getSimpleName() + " ----");
        Log.d(TAG_INTENT, "Action: " + intent.getAction());
        Log.d(TAG_INTENT, "Flags: " + intent.getFlags());

        Uri data = intent.getData();
        Log.d(TAG_INTENT, "Data: " + (data != null ? data : "null"));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d(TAG_INTENT, "Extra[" + key + "]=" + extras.get(key));
            }
        }
    }

    /* ---------------------------------- */
    /* Push / Deep link helpers            */
    /* ---------------------------------- */

    private void handlePushDismiss(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null || intent.getExtras() == null) return;

        Bundle payload = intent.getExtras();
        String ptId = payload.getString("pt_id");
        if (ptId == null) return;

        String ptDismiss = payload.getString(PTConstants.PT_DISMISS_ON_CLICK);
        boolean autoCancel = false;
        String actionId = payload.getString("actionId");
        int notificationId = payload.getInt("notificationId", -1);
        if (actionId != null) {
            Log.d("ACTION_ID", actionId);
            autoCancel = payload.getBoolean("autoCancel", true);
        }

//        if ((autoCancel || ptDismiss.equals(true)) && nm!=null && notificationId>-1)


        NotificationManager nm =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (autoCancel || notificationId > -1 && nm != null && payload.containsKey("notificationId")) {
            nm.cancel(payload.getInt("notificationId"));
        }
    }

    /* ---------------------------------- */
    /* CleverTap Init                      */
    /* ---------------------------------- */

    private void initCleverTap(CleverTapAPI ct) {
        ActivityLifecycleCallback.register(this);
        CleverTapUtils.init(this);
        CleverTapAPI.setNotificationHandler(new PushTemplateNotificationHandler());

        ct.setCTPushNotificationListener(this);
        ct.setInAppNotificationButtonListener(this);
        ct.enableDeviceNetworkInfoReporting(true);
        ct.syncRegisteredInAppTemplates();

        ct.getCleverTapID(new OnInitCleverTapIDListener() {
            @Override
            public void onInitCleverTapID(String cleverTapID) {
                firebaseAnalytics.setUserProperty("ct_objectId", cleverTapID);
            }
        });
    }

    private void initProductExperiences(CleverTapAPI ct) {
        ct.parseVariables(new PEVariables(this));
        ct.syncVariables();
    }

    private void initGeofence(CleverTapAPI ct) {
        CTGeofenceSettings settings = new CTGeofenceSettings.Builder()
                .setLocationFetchMode(FETCH_CURRENT_LOCATION_PERIODIC)
                .setLocationAccuracy(ACCURACY_HIGH)
                .enableBackgroundLocationUpdates(true)
                .setLogLevel(Logger.VERBOSE)
                .setGeofenceMonitoringCount(50)
                .setInterval(TimeUnit.MINUTES.toMillis(30))
                .build();

        CTGeofenceAPI.getInstance(this).init(settings, ct);
    }

    /* ---------------------------------- */
    /* Analytics Helpers                   */
    /* ---------------------------------- */

    private void trackAppBackground() {
        if (cleverTapAPI == null) return;
        HashMap<String, Object> props = new HashMap<>();
        props.put("state", "background");
        cleverTapAPI.pushEvent("App_Background", props);
        Log.d(TAG_LIFECYCLE, "App_Background event raised");
    }

    /* ---------------------------------- */
    /* CleverTap Callbacks                 */
    /* ---------------------------------- */

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Log.d("CTPushClick", payload.toString());
    }

    @Override
    public void onInAppButtonClick(HashMap<String, String> payload) {
        Log.d("CTInApp", payload.toString());
    }
}
