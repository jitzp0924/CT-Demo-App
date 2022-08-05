package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

public class webview extends AppCompatActivity {
    WebView myWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://jits-clever.github.io/TestWeb/");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new CTWebInterface(CleverTapAPI.getDefaultInstance(this)),"CleverTap");



    }
}