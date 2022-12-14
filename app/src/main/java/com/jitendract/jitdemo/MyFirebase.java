package com.jitendract.jitdemo;

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
import java.util.Map;

public class MyFirebase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        try {
            if (remoteMessage.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

                if (extras.containsKey("prog")) {

                    new ProgressTimer().createT(getApplicationContext(),extras);

                } else {
                    // NOt a progress bar timer template
                    new CTFcmMessageHandler().createNotification(getApplicationContext(), remoteMessage);
                }
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
