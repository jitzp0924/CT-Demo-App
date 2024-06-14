package com.jitendract.jitdemo;

import android.util.Log;

public class CTJTLoggger {

    public String tag,section;

    public CTJTLoggger(String section){
        this.section = section;
        this.tag = setTag(section);
    }

    public void E(String logMessage){
        Log.e(tag,logMessage);
    }

    public void D(String logMessage){
        Log.d(tag,logMessage);
    }
    public String setTag(String section) {
        switch (section){
            case "PE":
                return "PELogger";
            case "ND":
                return "NativeLogger";
            case "AI":
                return "AppInboxLogger";
            case "PIP":
                return "PiPLogger";
            default:
                return "CTJTLogger";

        }
    }
}
