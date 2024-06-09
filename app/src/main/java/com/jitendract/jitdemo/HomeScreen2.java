package com.jitendract.jitdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class HomeScreen2 extends AppCompatActivity {


    Map homeScreen, homeSlider;
    ImageView logout,search;
    Boolean searchFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_home_screen2);
        super.onCreate(savedInstanceState);

        logout = (ImageView) findViewById(R.id.logout_icon);
        search = (ImageView) findViewById(R.id.search_icon);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapDefaultInstance.fetchVariables();
        homeScreen = (Map<String,Object>) clevertapDefaultInstance.getVariableValue("HomeScreen");
        searchFlag = (Boolean) homeScreen.get("SearchIcon");
        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.remove("LoggedIn").apply();
            editor.remove("Identity").apply();

            Intent di = new Intent(getApplicationContext(),MainActivity.class);
            di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(di);
        });

        if (!searchFlag){
            search.setVisibility(View.INVISIBLE);
        }


        homeSlider = (Map) homeScreen.get("Bottom Carousel");
        sliderInit(clevertapDefaultInstance,homeSlider);
    }

    private void sliderInit(CleverTapAPI clevertapDefaultInstance, Map homeSlider) {

        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.homeCarousel);

        double counter = (double) homeSlider.get("Max Cards");
        counter = ((int) counter);
        for (int i = 1;i<=counter;i++){

            try {
                String card = (String) homeSlider.get("Card"+String.valueOf(i));
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
