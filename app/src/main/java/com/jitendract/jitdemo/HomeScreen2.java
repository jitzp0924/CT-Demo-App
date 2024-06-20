package com.jitendract.jitdemo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.card.MaterialCardView;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreen2 extends AppCompatActivity {


    Map<String,Object> homeScreen, homeSlider, recoForU;
    Map<String,Integer> payBill,quickLinks;
    HashMap<String, Object> homeScreenEvt, slidermap;
    String phoneNum,UserId;
    Double recoCards,counter;
    ImageView logout,search;
    Boolean searchFlag;
    LinearLayout fdrdlayout,investmentlayout,creditcardlayout,loanslayout,sendmoneylayout,serviceslayout,fixedreturnslayout,billpaylayout, fastaglayout,recharge,electricity,pipedgas,dth,broadband ;
    MaterialCardView recoCard1,recoCard2,recoCard3;
    SharedPreferences prefs;
    Button recoCardButton1,recoCardButton2,recoCardButton3;

    CleveTapUtils cleveTapUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeScreenEvt = new HashMap<>(); // Added initialization
        slidermap = new HashMap<>();

        setContentView(R.layout.activity_home_screen2);
        super.onCreate(savedInstanceState);

        slidermap = new HashMap<>();

        logout = findViewById(R.id.logout_icon);
        search = findViewById(R.id.search_icon);
        recoCard1 = findViewById(R.id.reco_card_1);
        recoCard2 = findViewById(R.id.reco_card_2);
        recoCard3 = findViewById(R.id.reco_card_3);
        recoCardButton1=findViewById(R.id.reco_card_1_button);
        recoCardButton2=findViewById(R.id.reco_card_2_button);
        recoCardButton3=findViewById(R.id.reco_card_3_button);

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
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","Fastag");
        });

        recharge.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","Recharge");
        });

        dth.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","DTH");
        });

        pipedgas.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","Piped Gas");
        });

        broadband.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","Broadband");
        });

        electricity.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen2.this, FDHome.class);
            startActivity(intent);
            commonOnClick("Pay Bills","Electricity");
        });



        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        phoneNum =prefs.getString("Phone","NA");
        UserId = prefs.getString("Identity","default");
        cleveTapUtils=new CleveTapUtils(getApplicationContext());
        homeScreenEvt.put("Phone",phoneNum);
        homeScreenEvt.put("UserId",UserId);
        homeScreenEvt.put("Screen","HomeScreen");

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.fetchVariables();
            try {
                homeScreen = (Map<String, Object>) clevertapDefaultInstance.getVariableValue("HomeScreen");
                recoForU = (Map<String, Object>) homeScreen.get("RecommendedForU");
                homeSlider = (Map<String, Object>) homeScreen.get("Bottom Carousel");
                quickLinks = convertValuesToInteger((Map<String, Object>) homeScreen.get("QuickLinks"));
                payBill = convertValuesToInteger((Map<String, Object>) homeScreen.get("Pay Bills"));
            }
            catch(Exception e){Log.e("PEException",String.valueOf(e));}

        }
        searchFlag = (Boolean) homeScreen.get("SearchIcon");
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

        if (homeSlider != null) {
            sliderInit(clevertapDefaultInstance,homeSlider);
        }

        if (payBill != null){
            payBillReorder(payBill);
        }

        if (quickLinks != null){
            rearrangeInnerLinearLayouts(quickLinks);
        }

        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.remove("LoggedIn").apply();
            editor.remove("Identity").apply();

            Intent di = new Intent(getApplicationContext(),MainActivity.class);
            di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(di);
        });

        recoCardButton1.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 1");
            cleveTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");
        });

        recoCardButton2.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 2");
            cleveTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");

            showPaymentSuccessPopup();

        });

        recoCardButton3.setOnClickListener(view -> {
            homeScreenEvt.put("Action","Click");
            homeScreenEvt.put("Label", "Card 3");
            cleveTapUtils.raiseEvent("Recommended For You",homeScreenEvt);
            homeScreenEvt.remove("Action");
            homeScreenEvt.remove("Label");
            System.out.println("Reco 3 raised");

            //Redirection
            HashMap<String, Object> redirectionDetails = new HashMap<>();
            redirectionDetails.put("ServiceID", "123");
            redirectionDetails.put("IsServiceActive", false);
            redirectionDetails.put("Deeplink", "https://developer.clevertap.com/docs/android");
            DeeplinkRedirection deeplinkRedirection = new DeeplinkRedirection(this);
            deeplinkRedirection.handleRedirection(redirectionDetails);
        });
    }

    public void commonOnClick(String text1, String text2) {
        if (cleveTapUtils != null) {
            cleveTapUtils.raiseEvent(text1, createEventProperties(text2));
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

    private void rearrangeInnerLinearLayouts(Map<String, Integer> quickLinks) {
        LinearLayout parentLayout1 = findViewById(R.id.verticalrow1);
        LinearLayout parentLayout2 = findViewById(R.id.verticalrow2);

        // Create a list of Map entries sorted by their values
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(quickLinks.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String layoutId = entry.getKey();
            LinearLayout layout = findViewById(getResources().getIdentifier(layoutId, "id", getPackageName()));

            // Remove the LinearLayout from its current position
            parentLayout1.removeView(layout);
            parentLayout2.removeView(layout);

            // Add the LinearLayout to the appropriate parent layout based on its position
            if (count < 4) {
                parentLayout1.addView(layout);
            } else {
                parentLayout2.addView(layout);
            }

            count++;
        }
    }



    private void payBillReorder(Map<String, Integer> payBill) {

        Map<String, Integer> payBills = (Map<String, Integer>) this.payBill; // Assuming this.payBills is a Map<String, Object>
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
}

