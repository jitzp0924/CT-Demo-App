package com.jitendract.jitdemo;

import android.content.Context;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

import java.util.HashMap;

public class CleveTapUtils {

    CleverTapAPI clevertapDefaultInstance;
    Context applicationContext;

    public CleveTapUtils(Context applicationContext) {

        this.applicationContext = applicationContext;
        this.clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext);
    }

    public void login(HashMap<String, Object> loginMap) {
        clevertapDefaultInstance.onUserLogin(loginMap);
    }

    public void raiseEvent(String evtName, HashMap<String, Object> evtMAp) {
        clevertapDefaultInstance.pushEvent(evtName,evtMAp);
    }

    public void CTinit(Context applicationContext){

        
        //        Multi Instance
        CleverTapInstanceConfig clevertapDefaultInstance2 =  CleverTapInstanceConfig.createInstance(applicationContext, "65R-654-5Z6Z", "456-256");
    }
}
