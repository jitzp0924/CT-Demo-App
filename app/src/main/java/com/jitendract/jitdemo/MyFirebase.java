package com.jitendract.jitdemo;

import android.app.Service;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebase extends FirebaseMessagingService {

//    @Override
//    public void onNewToken(@NonNull String s) {
//        String fcmRegId = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                String fcmtoken = task.getResult();
//                CleverTapAPI.getDefaultInstance(getApplicationContext()).pushFcmRegistrationId(fcmtoken,true);
//            }
//        });
//        super.onNewToken(s);
//    }


}
