package com.jitendract.jitdemo;

import static java.lang.Boolean.TRUE;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.signedcall.exception.InitException;
import com.clevertap.android.signedcall.init.SignedCallAPI;
import com.clevertap.android.signedcall.init.SignedCallInitConfiguration;
import com.clevertap.android.signedcall.init.p2p.FCMProcessingNotification;
import com.clevertap.android.signedcall.interfaces.SignedCallInitResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeScreen2 extends AppCompatActivity implements CTInboxListener,OnMapReadyCallback {


    Map<String,Object> homeScreen, homeSlider, recoForU,J4U;
    Map<String,Integer> payBill,quickLinks,rapidoLinks;
    HashMap<String, Object> homeScreenEvt, slidermap,pushPer;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_CUSTOM_INBOX_ENABLED = "custom_inbox_enabled";
    String phoneNum,UserId,appType;
    androidx.appcompat.widget.Toolbar toolbar;
    Double recoCards,counter;
    ImageView logout,search,profile_setting,appInboxButton,callIcon;
    Boolean searchFlag,locationPermissionGranted,callFlag;
    LinearLayout fdrdlayout,investmentlayout,creditcardlayout,loanslayout,sendmoneylayout,serviceslayout,fixedreturnslayout,billpaylayout, fastaglayout,recharge,electricity,pipedgas,dth,broadband ;
    LinearLayout verticalRow1, verticalRow2;
    MaterialCardView recoCard1,recoCard2,recoCard3;
    SharedPreferences prefs,sharedPreferences;
    Button recoCardButton1,recoCardButton2,recoCardButton3;
    LinearLayout recoSection,icoCar,icoAuto,icoBike,rapidoOptions,mapLayout1;
    SupportMapFragment mapFragment;
    TextView reco_card_1_text;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    CleverTapUtils cleverTapUtils;
    private GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeScreenEvt = new HashMap<>(); // Added initialization
        pushPer = new HashMap<>();
        slidermap = new HashMap<>();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            clevertapDefaultInstance.initializeInbox();
        }

        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        phoneNum =prefs.getString("Phone","NA");
        UserId = prefs.getString("Identity","default");

        setContentView(R.layout.activity_home_screen2);
        super.onCreate(savedInstanceState);

        setIdViews();
        setListners();
//        callingInit(clevertapDefaultInstance,UserId);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationPermissionGranted = prefs.getBoolean("locationPermissionGranted",TRUE);
        cleverTapUtils= CleverTapUtils.getInstance();
        homeScreenEvt.put("Phone",phoneNum);
        homeScreenEvt.put("UserId",UserId);
        homeScreenEvt.put("Screen","HomeScreen");

        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.fetchVariables();
            try {
                homeScreen = (Map<String, Object>) clevertapDefaultInstance.getVariableValue("HomeScreen");
                J4U = (Map<String, Object>) homeScreen.get("J4U");
//                JSONObject r4u = new JSONObject(String.valueOf(J4U.get("43")));
//                String titleReco = r4u.getString("title_text");
//                reco_card_1_text.setText(titleReco);


                recoForU = (Map<String, Object>) homeScreen.get("RecommendedForU");
                homeSlider = (Map<String, Object>) homeScreen.get("Bottom Carousel");
                quickLinks = convertValuesToInteger((Map<String, Object>) homeScreen.get("QuickLinks"));
                rapidoLinks = convertValuesToInteger((Map<String, Object>) homeScreen.get("rapidoLink"));
                payBill = convertValuesToInteger((Map<String, Object>) homeScreen.get("Pay Bills"));
                appType = String.valueOf(clevertapDefaultInstance.getVariableValue("appType"));
                searchFlag = (Boolean) homeScreen.get("SearchIcon");
                callFlag = (Boolean) homeScreen.get("Call");

            }
            catch(Exception e){Log.e("PEException",String.valueOf(e));}

        }

        if (appType.equals("rapido")){
            recoSection.setVisibility(View.GONE);
            verticalRow1.setVisibility(View.GONE);
            verticalRow2.setVisibility(View.GONE);
            rapidoOptions.setVisibility(View.VISIBLE);
            mapLayout1.setVisibility(View.VISIBLE);

            toolbar.setBackgroundColor(Color.parseColor("#f9c935"));

            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapLayout, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);

        }

        if(recoForU.get("MaxCard") instanceof Double){
            recoCards = (Double) recoForU.get("MaxCard");
        }
        if(recoForU.get("MaxCard") instanceof Integer){
            recoCards = Double.valueOf(String.valueOf(recoForU.get("MaxCard")));
        }

        if (recoCards == 1.0){
            recoCard3.setVisibility(View.GONE);
            recoCard2.setVisibility(View.GONE);
        } else if (recoCards == 2.0) {
            recoCard3.setVisibility(View.GONE);
        }else {
            Log.v("PEException","All Recommended for you Cards are visible");
        }

        if (!searchFlag){
            search.setVisibility(View.INVISIBLE);
        }

        if (!callFlag){
            callIcon.setVisibility(View.INVISIBLE);
        }

        if (homeSlider != null) {
            sliderInit(clevertapDefaultInstance,homeSlider);
        }

        if (payBill != null){
            payBillReorder(payBill);
        }

        if (quickLinks != null && !Objects.equals(appType, "rapido")){
            rearrangeInnerLinearLayouts(quickLinks);
        }
        if (rapidoLinks != null){
            rearrangeInnerLinearLayouts(rapidoLinks);
        }







        cleverTapUtils.raiseEvent("Home Screen",homeScreenEvt);
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        pushPer.put("Status","Accepted");
                        cleverTapUtils.raiseEvent("Permission Request",pushPer);
                        CleverTapAPI.createNotificationChannel(getApplicationContext(),"JitDemo","JitDemo","JitDemo", NotificationManager.IMPORTANCE_MAX,true);
                        CleverTapAPI.createNotificationChannel(getApplicationContext(),"r2d2","r2d2","r2d2 sound bad", NotificationManager.IMPORTANCE_MAX,true);
                        CleverTapAPI.createNotificationChannel(getApplicationContext(),"jiosound","jiosound","For JIO", NotificationManager.IMPORTANCE_MAX,true,"jiosound.mp3");
                        // Permission granted: You can show notifications
                    } else {
                        pushPer.put("Status","Denied");
                        cleverTapUtils.raiseEvent("Permission Request",pushPer);
                        // Permission denied: Inform user that notifications won't work
                    }
                }
        );
        askNotificationPermission();

        getCurrentLocationforLatLong(clevertapDefaultInstance);

    }

    private void askNotificationPermission() {
        pushPer.put("Type","PUSH");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                cleverTapUtils.raiseEvent("Permission Request",pushPer);
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    private void getCurrentLocationforLatLong(CleverTapAPI clevertapDefaultInstance) {

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location lastKnownLocation = task.getResult();
//                            Log.v("Location", lastKnownLocation.getLatitude() + " Longitude : " + lastKnownLocation.getLongitude());
                            clevertapDefaultInstance.setLocation(lastKnownLocation);
                        } else {
                            Log.v("Location", "Current location is null. Using defaults.");
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }

    private void setIdViews() {

        //profile setting
        profile_setting = findViewById(R.id.profile_icon);
        //App Inbox
        appInboxButton = findViewById(R.id.notification_icon);
        toolbar = findViewById(R.id.toolbar);

        logout = findViewById(R.id.logout_icon);
        search = findViewById(R.id.search_icon);
        callIcon = findViewById(R.id.call_icon);
        recoSection = findViewById(R.id.recoSection);
        verticalRow1 = findViewById(R.id.verticalrow1);
        verticalRow2 = findViewById(R.id.verticalrow2);

        recoCard1 = findViewById(R.id.reco_card_1);
        recoCard2 = findViewById(R.id.reco_card_2);
        recoCard3 = findViewById(R.id.reco_card_3);
        recoCardButton1=findViewById(R.id.reco_card_1_button);
        recoCardButton2=findViewById(R.id.reco_card_2_button);
        recoCardButton3=findViewById(R.id.reco_card_3_button);
        reco_card_1_text = findViewById(R.id.reco_card_1_text);

        fastaglayout = findViewById(R.id.Fastag);
        electricity = findViewById(R.id.Electricity);
        dth = findViewById(R.id.DTH);
        recharge = findViewById(R.id.Recharge);
        pipedgas = findViewById(R.id.PipedGas);
        broadband = findViewById(R.id.Broadband);

        fdrdlayout = findViewById(R.id.FDRD);
        investmentlayout = findViewById(R.id.Investments);
        creditcardlayout = findViewById(R.id.CreditCard);
        loanslayout = findViewById(R.id.Loans);
        sendmoneylayout = findViewById(R.id.SendMoney);
        serviceslayout = findViewById(R.id.Services);
        billpaylayout = findViewById(R.id.BillPay);
        fixedreturnslayout = findViewById(R.id.FixedReturns);

        icoAuto = findViewById(R.id.icoAuto);
        icoBike = findViewById(R.id.icoBike);
        icoCar = findViewById(R.id.icoCar);
        rapidoOptions = findViewById(R.id.rapidoOptions);
        mapLayout1 = findViewById(R.id.mapLayout);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLayout);

    }


    private void callingInit(CleverTapAPI cleverTapAPI, @NonNull String UserId) {

        JSONObject initOptions = new JSONObject();
        Boolean allowPersistSocketConnection = true;
        FCMProcessingNotification fcmProcessingNotification = null;
        String notiTitle = "Hey" + UserId.toUpperCase();
        String notiSubtitle = "You have an incoming call ....";
        int fcmNotificationLargeIcon = R.drawable.smicon1;
        String cancelCta = "Reject";

        try {
            initOptions.put("accountId","67a9ead27be487e18d1681ed");
            initOptions.put("apiKey","M9eULHgg2CgJP4wJ53jKpCUYQMu14FemJLXH4WLuQvN35u3VRxuUDW8zP8SEZRJV");
            initOptions.put("cuid",UserId);
            initOptions.put("appId", BuildConfig.APPLICATION_ID);
            initOptions.put("name",UserId.toUpperCase());
//            initOptions.put("ringtone", <string / optional>);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("SignedCAll","Error in SignedCall InitOptions");
        }

        try {
            fcmProcessingNotification = new FCMProcessingNotification.Builder(notiTitle, notiSubtitle)
                    .setLargeIcon(fcmNotificationLargeIcon)
                    .setCancelCtaLabel(cancelCta)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SignedCAll","Error in SignedCall FCMProcessingNotification");
        }

        SignedCallInitResponse signedCallInitResponse = new SignedCallInitResponse() {
            @Override
            public void onSuccess() {
                Log.v("SignedCAll","Successful in SignedCall Listener");

            }

            @Override
            public void onFailure(@NonNull InitException initException) {
                Log.d("SignedCall: ", "error code: " + initException.getErrorCode()
                        + "\n error message: " + initException.getMessage()
                        + "\n error explanation: " + initException.getExplanation());

                if (initException.getErrorCode() == InitException.SdkNotInitializedException.getErrorCode()) {
                    //Handle this error here
                }
            }
        };



        //Create a Builder instance of SignedCallInitConfiguration and pass it inside the init() method
        SignedCallInitConfiguration initConfiguration = new SignedCallInitConfiguration.Builder(initOptions, allowPersistSocketConnection)
                .promptReceiverReadPhoneStatePermission(true)
                .setFCMProcessingMode(SignedCallInitConfiguration.FCMProcessingMode.BACKGROUND,fcmProcessingNotification)
                .build();

        SignedCallAPI.getInstance().init(getApplicationContext(), initConfiguration, cleverTapAPI, signedCallInitResponse);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        boolean customInboxEnabled = sharedPreferences.getBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
//        if (customInboxEnabled) {
//            customappInboxButton.setVisibility(View.VISIBLE);
//            appInboxButton.setVisibility(View.GONE);
//        } else {
//            appInboxButton.setVisibility(View.VISIBLE);
//            customappInboxButton.setVisibility(View.GONE);
//        }
//    }


    public void commonOnClick(String text1, String text2) {
        if (cleverTapUtils != null) {
            cleverTapUtils.raiseEvent(text1, createEventProperties(text2));
        }
    }

    private HashMap<String, Object> createEventProperties(String text2) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("Action", "Click");
        properties.put("Label", text2);
        properties.put("Screen", "Home Screen");
        return properties;
    }

    private Map<String, Integer> convertValuesToInteger(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Map<String, Integer> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Double) {
                resultMap.put(entry.getKey(), ((Double) value).intValue());
                int results = resultMap.put(entry.getKey(), ((Double) value).intValue());
                Log.v("Double to integer:",entry.getValue() + " converted to " + results);
            } else if (value instanceof Integer) {
                resultMap.put(entry.getKey(), (Integer) value);
            } else {
                // Handle other cases if needed
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }
        }
        return resultMap;
    }

    private void rearrangeInnerLinearLayouts(@NonNull Map<String, Integer> quickLinks) {

        LinearLayout parentLayout1 = findViewById(R.id.verticalrow1);
        LinearLayout parentLayout2 = findViewById(R.id.verticalrow2);
        LinearLayout rapidoOptions = findViewById(R.id.rapidoOptions);


        // Create a list of Map entries sorted by their values
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(quickLinks.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String layoutId = entry.getKey();
            LinearLayout layout = findViewById(getResources().getIdentifier(layoutId, "id", getPackageName()));

            // Remove the LinearLayout from its current position
            if (appType.equals("rapido")){
                if (layout.getParent() != null){((ViewGroup)layout.getParent()).removeView(layout);}

            }
            else {
                parentLayout1.removeView(layout);
                parentLayout2.removeView(layout);
            }


            // Add the LinearLayout to the appropriate parent layout based on its position
            if (count < 4) {
                if (appType.equals("rapido")){rapidoOptions.addView(layout);}else{parentLayout1.addView(layout);}

            } else {
                parentLayout2.addView(layout);
            }

            count++;
        }
    }



    private void payBillReorder(Map<String, Integer> payBill) {

        Map<String, Integer> payBills = this.payBill; // Assuming this.payBills is a Map<String, Object>
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(payBills.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));


        LinearLayout parentLayout = findViewById(R.id.parentLayout);

        // Bharatpay watermark
        LinearLayout lastLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params1.setMargins(
                0, // left margin in pixels
                0, // top margin in pixels
                0, // right margin in pixels
                0  // bottom margin in pixels
        );
        lastLinearLayout.setLayoutParams(params1);
        lastLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        lastLinearLayout.setGravity(Gravity.END); // Aligns content to the end

        // Create TextViews for "Offered By" and "Bharat BillPay"
        TextView textViewOfferedBy = new TextView(this);
        textViewOfferedBy.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textViewOfferedBy.setText("Offered By ");
        lastLinearLayout.addView(textViewOfferedBy);

        TextView textViewBharatBillPay = new TextView(this);
        textViewBharatBillPay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textViewBharatBillPay.setText("Bharat BillPay");
        textViewBharatBillPay.setTextColor(Color.parseColor("#f27823"));
        textViewBharatBillPay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textViewBharatBillPay.setTypeface(null, Typeface.BOLD);
        lastLinearLayout.addView(textViewBharatBillPay);

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String layoutId = entry.getKey();
            LinearLayout layout = findViewById(getResources().getIdentifier(layoutId, "id", getPackageName()));

            // Remove the LinearLayout from its current position
            parentLayout.removeView(layout);

            // Create a new divider view instance for each iteration
            View dividerView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1 // Height of the divider view
            );
            params.setMargins(0, 0, 0, 0); // Set top margin for the divider view
            dividerView.setLayoutParams(params);
            dividerView.setBackgroundColor(getResources().getColor(R.color.bank_dark_secondary));

            // Add the LinearLayout and the divider view to the parent layout in the sorted order
            parentLayout.addView(layout);
            parentLayout.addView(dividerView);
        }

// Add the last LinearLayout to the parent layout
        parentLayout.addView(lastLinearLayout);
    }

    private void sliderInit(CleverTapAPI clevertapDefaultInstance, Map homeSlider) {
        HashMap<String, Object> slidermap = new HashMap<>();
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.homeCarousel);
        slidermap.put("eventName","Bottom Carousel");

        if(homeSlider.get("Max Cards") instanceof Double){
            counter = (Double) homeSlider.get("Max Cards");
        }
        if(homeSlider.get("Max Cards") instanceof Integer){
            counter = Double.valueOf(String.valueOf(homeSlider.get("Max Cards")));
        }


        for (int i = 1;i<=counter;i++){

            try {
                String card = (String) homeSlider.get("Card"+ i);
                JSONObject jsonObject = new JSONObject(card);
                Log.e("PEException",jsonObject.getString("imageUrl"));
                sliderDataArrayList.add(new SliderData(jsonObject.getString("imageUrl")));
                slidermap.put(String.valueOf(i),jsonObject.getString("imageUrl"));
            }
            catch (Exception e){
                Log.e("PEException",String.valueOf(e));
            }
        }
        // passing this array list inside our adapter class.
        String evtName = "Bottom Carousel";
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList,slidermap, homeScreenEvt);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(3);

        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();
    }

    private void showPaymentSuccessPopup() {
        Dialog dialog = new Dialog(this, R.style.PopupAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_success);

        ImageView successImage = dialog.findViewById(R.id.success_image);
        TextView successText = dialog.findViewById(R.id.success_text);
        Button okButton = dialog.findViewById(R.id.ok_button);

        // Set up any additional properties, listeners, etc.
        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void inboxDidInitialize() {
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }

    private void setListners(){

        callIcon.setOnClickListener(view -> {
            CallReceiveDailog callReceiveDailog = new CallReceiveDailog();
            callReceiveDailog.show(getSupportFragmentManager(),"CallReceiveDailog");
        });

        profile_setting.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, Settings.class);
            startActivity(intent);
        });

        fdrdlayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","FD/RD");

        });

        investmentlayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Investments");

        });

        creditcardlayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Credit Card");

        });

        loanslayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Loans");

        });

        sendmoneylayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Send Money");

        });

        serviceslayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Services");

        });

        fixedreturnslayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Fixed Returns");

        });

        billpaylayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Quick Links","Bill Pay");
        });

        fastaglayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "Fastag");
            startActivity(intent);
            commonOnClick("Pay Bills","Fastag");
        });

        recharge.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "Recharge");
            startActivity(intent);
            commonOnClick("Pay Bills","Recharge");
        });

        dth.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "DTH");
            startActivity(intent);
            commonOnClick("Pay Bills","DTH");
        });

        pipedgas.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "Piped Gas");
            startActivity(intent);
            commonOnClick("Pay Bills","Piped Gas");
        });

        broadband.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "Broadband");
            startActivity(intent);
            commonOnClick("Pay Bills","Broadband");
        });

        electricity.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, MultiTaskPayBills.class);
            intent.putExtra("category", "Electricity");
            startActivity(intent);
            commonOnClick("Pay Bills","Electricity");
        });


        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.remove("LoggedIn").apply();
            editor.remove("Identity").apply();

            Log.i("SignedCall","Calling Logout for Signed Call");
            SignedCallAPI.getInstance().logout(getApplicationContext());

            Intent di = new Intent(getApplicationContext(),MainActivity.class);
            di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(di);
        });

        recoCardButton1.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 1");
            cleverTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");
        });

        recoCardButton2.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 2");
            cleverTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");

            showPaymentSuccessPopup();

        });

        recoCardButton3.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 3");
            cleverTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");
            System.out.println("Reco 3 raised");

            //Redirection
            HashMap<String, Object> redirectionDetails = new HashMap<>();
//            redirectionDetails.put("ServiceID", "123");
            redirectionDetails.put("IsServiceActive", false);
            redirectionDetails.put("Deeplink", "https://developer.clevertap.com/docs/android");
            DeeplinkRedirection deeplinkRedirection = new DeeplinkRedirection(this);
            deeplinkRedirection.handleRedirection(redirectionDetails);
        });


        icoBike.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Bike","Quick Links");

        });


        icoAuto.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Auto","Quick Links");
        });


        icoCar.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Car","Quick Links");
        });

        appInboxButton.setOnClickListener(view -> {

            //Custom Inbox Logic
            boolean customInboxEnabled = sharedPreferences.getBoolean(KEY_CUSTOM_INBOX_ENABLED, false);
            Log.v("CUSTOM INBOX VALUE",customInboxEnabled + "");

            if (customInboxEnabled) {
                Intent intent = new Intent(HomeScreen2.this, CustomInboxActivity.class);
                startActivity(intent);
            } else {
                if (cleverTapUtils.getDefaultInstance() != null) {
                    ArrayList<String> tabs = new ArrayList<>();
                    tabs.add("Promotions");
                    tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored

                    CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
                    styleConfig.setFirstTabTitle("First Tab");
                    styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
                    styleConfig.setTabBackgroundColor("#FF0000");
                    styleConfig.setSelectedTabIndicatorColor("#0000FF");
                    styleConfig.setSelectedTabColor("#0000FF");
                    styleConfig.setUnselectedTabColor("#FFFFFF");
                    styleConfig.setBackButtonColor("#FF0000");
                    styleConfig.setNavBarTitleColor("#FF0000");
                    styleConfig.setNavBarTitle("MY INBOX");
                    styleConfig.setNavBarColor("#FFFFFF");
                    styleConfig.setInboxBackgroundColor("#ADD8E6");
                    cleverTapUtils.getDefaultInstance().showAppInbox(styleConfig); //With Tabs
                }
            }
        });

    }



    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        this.map = map;
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        LatLng sydney = new LatLng(-33.852, 151.211);
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location lastKnownLocation = task.getResult();

                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()),14F));
                            }
                        } else {
                            Log.d("Location", "Current location is null. Using defaults.");
                            Log.e("Location", "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(sydney, 14F));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}

