package com.jitendract.jitdemo;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.jitendract.jitdemo.CtPE.PEVariables;
import java.util.HashMap;

public class application extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, CTPushNotificationListener {

    public static AppDatabase database;
    @Override
    public void onCreate() {

        ActivityLifecycleCallback.register(this);
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());

        assert cleverTapAPI != null;
        PEInit(cleverTapAPI);
        CleverTapAPI.setNotificationHandler(new PushTemplateNotificationHandler());
        cleverTapAPI.setCTPushNotificationListener(this);
        CleverTapAPI.getDefaultInstance(this).enableDeviceNetworkInfoReporting(true);
        cleverTapAPI.enableDeviceNetworkInfoReporting(true);

        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "transactions-db").build();
    }

    private void PEInit(CleverTapAPI cleverTapAPI) {

        PEVariables peVariables = new PEVariables(getApplicationContext());
        cleverTapAPI.parseVariables(peVariables);
        cleverTapAPI.syncVariables();

    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

        Bundle payload = activity.getIntent().getExtras();
        if (payload.containsKey("pt_id")&& payload.getString("pt_id").equals("pt_rating"))
        {
            NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(payload.getInt("notificationId"));
        }
        if (payload.containsKey("pt_id")&& payload.getString("pt_id").equals("pt_product_display"))
        {
            NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(payload.getInt("notificationId"));
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {

        Log.e("CleverTap Click", String.valueOf(payload));
        String p1 = String.valueOf(payload);
        Toast.makeText(this,p1, Toast.LENGTH_LONG).show();

    }


}
