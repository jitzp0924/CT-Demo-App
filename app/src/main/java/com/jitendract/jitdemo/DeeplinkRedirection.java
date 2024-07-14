package com.jitendract.jitdemo;

import android.content.Intent;

import java.util.HashMap;

import android.content.Context;


public class DeeplinkRedirection {

    private final Context context;

    // Constructor to pass the context
    public DeeplinkRedirection(Context context) {
        this.context = context;
    }

    public String handleRedirection(HashMap<String, Object> redirectMap) {
        String serviceId = (String) redirectMap.get("ServiceID");
        Boolean isServiceActive = (Boolean) redirectMap.get("IsServiceActive");
        String deeplink = (String) redirectMap.get("Deeplink");

        if (isServiceActive != null && isServiceActive) {
            //logic to handle when ServiceActive is set to true
            return serviceId;
        } else {
            if (deeplink != null) {
                Intent webviewIntent = new Intent(context, webview.class);
                webviewIntent.putExtra("url", deeplink);
                context.startActivity(webviewIntent);
            }
            return null;
        }
    }
}
