package com.jitendract.jitdemo.JsInterface;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.core.content.pm.PackageInfoCompat;

import com.jitendract.jitdemo.nudge.FloatingBullet;
import com.jitendract.jitdemo.nudge.showPipOverlay;

import org.json.JSONException;
import org.json.JSONObject;

public class jInterface {
    Context mContext;
    SharedPreferences prefs;
    boolean isLoggedIN;
    String userID,appVersion;
    PackageManager packageManager;
    PackageInfo packageInfo;

    public jInterface(Context context) {
        this.mContext = context;
    }

    // Web → Android: Send data from web to Android
    @JavascriptInterface
    public void triggerPiP(String data) {
        Log.d("jInterface", "Data from Web: " + data);
        try {
            JSONObject pipObject = new JSONObject(data);
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.runOnUiThread(() -> {
                    showPipOverlay.getInstance().show(activity, pipObject);
                });
            }
            else {
                Log.e("jInterface", "Context is not an Activity. Cannot show PiP overlay.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void triggerBullet(String data) {
        Log.d("jInterface", "Data from Web: " + data);
        try {
            JSONObject nudgeConfig = new JSONObject(data);
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.runOnUiThread(() -> {
                    FloatingBullet.show((Activity) mContext, nudgeConfig);
                });
            }
            else {
                Log.e("jInterface", "Context is not an Activity. Cannot show Bullet overlay.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Android → Web: Send data when JS calls getAppData()
    @JavascriptInterface
    public String getAppData() {
        JSONObject obj = new JSONObject();
        try {
            prefs = mContext.getSharedPreferences("Login", MODE_PRIVATE);
            isLoggedIN =prefs.getBoolean("LoggedIn",false);
            userID = prefs.getString("Identity",null);
            packageManager = mContext.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfo = packageManager.getPackageInfo(
                        mContext.getPackageName(),
                        PackageManager.PackageInfoFlags.of(0)
                );
            } else {
                packageInfo = packageManager.getPackageInfo(
                        mContext.getPackageName(),
                        0
                );
            }
            appVersion = packageInfo.versionName;


            obj.put("identity", userID);
            obj.put("source", "android_app/webview");
            obj.put("appVersion", appVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return obj.toString();
    }
}

