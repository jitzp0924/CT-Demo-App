package com.jitendract.jitdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.card.MaterialCardView;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeScreen2 extends AppCompatActivity {


    Map<String,Object> homeScreen, homeSlider, recoForU;
    HashMap<String, Object> homeScreenEvt = new HashMap<>();
    String phoneNum,UserId;
    Double recoCards,counter;
    ImageView logout,search;
    Boolean searchFlag;
    MaterialCardView recoCard1,recoCard2,recoCard3;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_home_screen2);
        super.onCreate(savedInstanceState);

        logout = findViewById(R.id.logout_icon);
        search = findViewById(R.id.search_icon);
        recoCard1 = findViewById(R.id.reco_card_1);
        recoCard2 = findViewById(R.id.reco_card_2);
        recoCard3 = findViewById(R.id.reco_card_3);
        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        phoneNum =prefs.getString("Phone","NA");
        UserId = prefs.getString("Identity","default");
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
            Log.v("PEExcwption","All Recommended for you Cards are visible");
        }

        if (!searchFlag){
            search.setVisibility(View.INVISIBLE);
        }

        if (homeSlider != null) {
            sliderInit(clevertapDefaultInstance,homeSlider);
        }

        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.remove("LoggedIn").apply();
            editor.remove("Identity").apply();

            Intent di = new Intent(getApplicationContext(),MainActivity.class);
            di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(di);
        });
    }

    private void sliderInit(CleverTapAPI clevertapDefaultInstance, Map homeSlider) {

        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.homeCarousel);

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
            }
            catch (Exception e){
                Log.e("PE-exception",String.valueOf(e));
            }
        }
        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

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


}
