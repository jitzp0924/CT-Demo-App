package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

import java.util.Date;
import java.util.HashMap;

public class webview extends AppCompatActivity {
    WebView myWebView;
    ImageView backBtn;
    String url = "https://in.bookmyshow.com/activities/nicco-park/ET00081908";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        myWebView = findViewById(R.id.webview);
        backBtn=findViewById(R.id.back_icon);
//        String url = getIntent().getStringExtra("url");
//        String htmlContent = "<!DOCTYPE html>\n" +
//                "<html lang=\"en\">\n" +
//                "<head>\n" +
//                "<meta charset=\"UTF-8\">\n" +
//                "<title>Cards</title>\n" +
//                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n" +
//                "<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
//                "<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
//                "<link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap\" rel=\"stylesheet\">\n" +
//                "<style>\n" +
//                ":root {\n" +
//                "--primary: #111926;\n" +
//                "--white: #fff;\n" +
//                "--background: #F8F8FF;\n" +
//                "--gray: #D3D3D3;\n" +
//                "--text: #262626;\n" +
//                "--like: #FF3040;\n" +
//                "}\n" +
//                "body, html { overflow: hidden; height: 100%; }\n" +
//                "body {\n" +
//                "font-family: 'Inter', sans-serif;\n" +
//                "}\n" +
//                "* {\n" +
//                "box-sizing: border-box;\n" +
//                "}\n" +
//                ".card {\n" +
//                "background: var(--white);\n" +
//                "border-radius: 1.5rem;\n" +
//                "box-shadow: rgba(0, 0, 0, 0.1) 0px 2px 6px;\n" +
//                "display: flex;\n" +
//                "flex-direction: column;\n" +
//                "overflow: hidden;\n" +
//                "cursor: pointer;\n" +
//                "transition: all 0.2s ease-in-out;\n" +
//                "flex: 0 0 22rem; /* Adjust according to your card width */\n" +
//                "margin-top: 2rem;\n" +
//                "}\n" +
//                ".card:hover {\n" +
//                "box-shadow: rgba(0, 0, 0, 0.1) 0px 6px 16px;\n" +
//                "}\n" +
//                ".card__preview {\n" +
//                "height: 100%;\n" +
//                "width: 100%;\n" +
//                "position: relative;\n" +
//                "overflow: hidden;\n" +
//                "}\n" +
//                ".card__preview img {\n" +
//                "width: 100%;\n" +
//                "object-fit: cover;\n" +
//                "transition: all 0.4s ease-out;\n" +
//                "}\n" +
//                ".card:hover .card__preview img {\n" +
//                "transform: scale(1.35);\n" +
//                "}\n" +
//                ".card__address {\n" +
//                "margin-top: 0.5rem;\n" +
//                "}\n" +
//                ".cards {\n" +
//                "display: flex;\n" +
//                "flex-wrap: wrap;\n" +
//                "gap: 1.5rem;\n" +
//                "justify-content: center;\n" +
//                "margin-top: 1rem;\n" +
//                "}\n" +
//                ".card__price {\n" +
//                "background-color: var(--white);\n" +
//                "color: var(--text);\n" +
//                "z-index: 1;\n" +
//                "position: absolute;\n" +
//                "bottom: 1.25rem;\n" +
//                "padding: 0.25rem 0.5rem;\n" +
//                "border-radius: 0.5rem;\n" +
//                "left: 1.25rem;\n" +
//                "font-weight: bold;\n" +
//                "}\n" +
//                ".card__content {\n" +
//                "padding: 1.25rem;\n" +
//                "display: flex;\n" +
//                "flex-direction: column;\n" +
//                "flex-grow: 1;\n" +
//                "color: var(--text);\n" +
//                "}\n" +
//                ".card__bottom {\n" +
//                "margin-top: auto;\n" +
//                "display: flex;\n" +
//                "justify-content: space-between;\n" +
//                "align-items: center;\n" +
//                "}\n" +
//                ".card__description {\n" +
//                "margin-top: 0.5rem;\n" +
//                "display: -webkit-box;\n" +
//                "overflow: hidden;\n" +
//                "-webkit-box-orient: vertical;\n" +
//                "-webkit-line-clamp: 5;\n" +
//                "}\n" +
//                "/* Your CSS styles here */\n" +
//                ".carousel {\n" +
//                "overflow: hidden;\n" +
//                "width: 22rem; /* Adjust the width according to your card width */\n" +
//                "margin: 0 auto;\n" +
//                "position: relative;\n" +
//                "}\n" +
//                ".carousel__content {\n" +
//                "display: flex;\n" +
//                "transition: transform 0.5s ease-in-out;\n" +
//                "width: calc(22rem * 3); /* Adjust the width according to the number of cards */\n" +
//                "}\n" +
//                "</style>\n" +
//                "</head>\n" +
//                "<body>\n" +
//                "<div class=\"carousel\">\n" +
//                "<div class=\"carousel__content\">\n" +
//                "<div class=\"card\">\n" +
//                "<div class=\"card__preview\"><a href=\"https://in.bookmyshow.com/activities/aadrai-trek-the-unexplored-jungle-trek/ET00317162?utm_source=McDowellsYaariJamftSanamRitvizZaeden09July2024&utm_medium=email&utm_campaign=Aadrai\" target=\"_blank\"><img src=\"https://assets-in.bmscdn.com/mailers/images/01072024/pn/aadrai_inapp.jpg\" alt=\"Aadrai Jungle Trek - Treks and Trails\"></a>\n" +
//                "</div>\n" +
//                "<!-- <div class=\"card__content\">\n" +
//                "<h2 class=\"card__title\">Japanese Castle</h2>\n" +
//                "<p class=\"card__address\">\n" +
//                "These Sliders Works on a automated basis Changing Every 3 seconds. The Duration is Configurable.\n" +
//                "</p>\n" +
//                "<p class=\"card__description\">\n" +
//                "Any Addtional Descriptions to be added here....<br>.<br>.<br>.\n" +
//                "</p>\n" +
//                "</div> -->\n" +
//                "</div>\n" +
//                "<div class=\"card\">\n" +
//                "<div class=\"card__preview\"><a href=\"https://in.bookmyshow.com/events/sagar-waali-qawwali-ft-dj-omen-live-in-kolkata/ET00395944?utm_source=McDowellsYaariJamftSanamRitvizZaeden09July2024&utm_medium=email&utm_campaign=Sagar\" target=\"_blank\"><img src=\"https://assets-in.bmscdn.com/mailers/images/01072024/pn/sagar_kolkata_inapp.jpg\" alt=\"Sagar Waali Qawwali Ft. DJ Omen - Live in Kolkata\"></a>\n" +
//                "</div>\n" +
//                "<!-- <div class=\"card__content\">\n" +
//                "<h2 class=\"card__title\">Japanese Gate</h2>\n" +
//                "<p class=\"card__address\">\n" +
//                "258 Serenity Lane, Crestwood Hills\n" +
//                "</p>\n" +
//                "</div> -->\n" +
//                "</div>\n" +
//                "<div class=\"card\">\n" +
//                "<div class=\"card__preview\"><a href=\"https://in.bookmyshow.com/plays/albattya-galbattya/ET00393587?utm_source=McDowellsYaariJamftSanamRitvizZaeden09July2024&utm_medium=email&utm_campaign=Albattya\" target=\"_blank\"><img src=\"https://assets-in.bmscdn.com/mailers/images/01062024/pn/albattya_galbattya_inapp.jpg\" alt=\"Albattya Galbattya\"></a>\n" +
//                "</div>\n" +
//                "<!-- <div class=\"card__content\">\n" +
//                "<h2 class=\"card__title\">Japanese Temple</h2>\n" +
//                "<p class=\"card__address\">\n" +
//                "258 Serenity Lane, Crestwood Hills\n" +
//                "</p>\n" +
//                "</div> -->\n" +
//                "</div>\n" +
//                "</div>\n" +
//                "</div>\n" +
//                "<script>\n" +
//                "const carousel = document.querySelector('.carousel');\n" +
//                "const carouselContent = document.querySelector('.carousel__content');\n" +
//                "const cardWidth = document.querySelector('.card').offsetWidth;\n" +
//                "\n" +
//                "let currentIndex = 0;\n" +
//                "\n" +
//                "function nextSlide() {\n" +
//                "currentIndex++;\n" +
//                "if (currentIndex >= carouselContent.children.length) {\n" +
//                "currentIndex = 0;\n" +
//                "}\n" +
//                "updateCarousel();\n" +
//                "}\n" +
//                "\n" +
//                "function updateCarousel() {\n" +
//                "const offset = -currentIndex * cardWidth;\n" +
//                "carouselContent.style.transform = `translateX(${offset}px)`;\n" +
//                "}\n" +
//                "\n" +
//                "setInterval(nextSlide, 3000); // Change slide every 3 seconds\n" +
//                "</script>\n" +
//                "</body>\n" +
//                "</html>";
//
//        String encodedHTML = Base64.encodeToString(htmlContent.getBytes(), Base64.NO_PADDING);
//        myWebView.loadData(encodedHTML, "text/html", "base64");

//        myWebView.loadUrl(url);



        backBtn.setOnClickListener(View->{
            Intent intent = new Intent(getApplicationContext(), HomeScreen2.class);
            startActivity(intent);
        });


        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        myWebView = (WebView) findViewById(R.id.webView);
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
//

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

//        myWebView.loadUrl("https://ad.admitad.com/g/vbnovi30pqd6b88a5577ed464edc45/");

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