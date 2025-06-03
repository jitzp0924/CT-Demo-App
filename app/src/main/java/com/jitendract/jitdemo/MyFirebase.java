package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;


import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyFirebase extends FirebaseMessagingService {
    CleverTapUtils cleverTapUtils;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        try {
            if (remoteMessage.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

                Log.d("FCM Payload", String.valueOf(extras));
                if (extras.containsKey("nm") || extras.containsKey("nt")){
                    HashMap<String, Object> payload = new HashMap<>();
                    for (String key : extras.keySet()) {
                        payload.put(key, extras.get(key));
                    }

                    CleverTapUtils cleverTapUtils = CleverTapUtils.getInstance();

//                    cleverTapUtils.raiseEvent("Silent Push",payload);
                }
                
                if (extras.containsKey("prog")) {

                    Intent intent = new Intent(this, ProgressTimer.class);
                    intent.putExtras(extras);
                    startService(intent);
//                    CleverTapAPI.processPushNotification(this,extras)

                } else {
                    // NOt a progress bar timer template
                    new CTFcmMessageHandler().createNotification(getApplicationContext(), remoteMessage);


                }
//                if(PushNotificationHandler.isForPushTemplates(extras)){
//                    new CTFcmMessageHandler().createNotification(getApplicationContext(), remoteMessage);
//                }
            }
        } catch (Throwable t) {
            Log.d("MYFCMLIST", "Error parsing FCM message", t);
        }
        super.onMessageReceived(remoteMessage);


    }

    @Override
    public void onNewToken(@NonNull String s) {



        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String fcmtoken = task.getResult();
                CleverTapAPI.getDefaultInstance(getApplicationContext()).pushFcmRegistrationId(fcmtoken,true);
            }
        });
        super.onNewToken(s);
    }


//    @Override
//    public void onNewToken(@NonNull String s) {
//        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        Task<InstallationTokenResult> fcmRegId = FirebaseInstallations.getInstance().getToken();
//        clevertapDefaultInstance.pushFcmRegistrationId(fcmRegId,true);
//        super.onNewToken(s);
//    }


}
