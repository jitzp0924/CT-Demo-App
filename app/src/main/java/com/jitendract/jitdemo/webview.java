package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

import java.util.Date;
import java.util.HashMap;

public class webview extends AppCompatActivity {
    WebView myWebView;
    String url = "https://in.bookmyshow.com/activities/nicco-park/ET00081908";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.addJavascriptInterface(new CTWebInterface(CleverTapAPI.getDefaultInstance(this)),"CleverTap");
        myWebView.loadUrl("https://in.bookmyshow.com/activities/nicco-park/ET00081908");
        CleverTapAPI.getDefaultInstance(this).resumeInAppNotifications();

        HashMap<String, Object> nt = new HashMap<String, Object>();
        nt.put("Date",new Date());
        nt.put("Screen","WebView");
        clevertapDefaultInstance.pushEvent("WebView Screen",nt);


    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        Log.e("ClevertapJT",intent.getStringExtra("id"));
    }

    public void webViewClick(View view) {
        myWebView.loadUrl("https://in.bookmyshow.com/venue/nicco-park-kolkata/NPKK");
    }

    public void chromeClick(View view) {

//        Intent i = new Intent("android.intent.action.MAIN");
//        i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
//        i.addCategory("android.intent.category.LAUNCHER");
//        i.setData(Uri.parse(url));
//        startActivity(i);

//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
//        i.setData(Uri. parse(url));
//        startActivity(i);

        myWebView.loadUrl("https://ad.admitad.com/g/vbnovi30pqd6b88a5577ed464edc45/");

//        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
//        openCustomTab(this, customIntent.build(), Uri.parse(url));

    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = "com.android.chrome";
//        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
//        }
    }
}