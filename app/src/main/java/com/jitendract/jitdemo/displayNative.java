package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class displayNative extends AppCompatActivity implements DisplayUnitListener {

    TextView title1,title2,msg1,msg2;
    ImageView img1,img2;
    CardView card1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_native);
        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);

        title1 = findViewById(R.id.title1);
        msg1 = findViewById(R.id.msg1);
        img1 = findViewById(R.id.img1);
        card1 = findViewById(R.id.card1);

    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        for (int i = 0; i <units.size() ; i++) {
            CleverTapDisplayUnit unit = units.get(i);
            try {
                prepareDisplayView(unit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void prepareDisplayView(CleverTapDisplayUnit unit) throws JSONException {


        String unitID = unit.getUnitID();
        String type = String.valueOf(unit.getType());
        String bgColor = unit.getBgColor();
        card1.setCardBackgroundColor(Color.parseColor(bgColor));
        title1.setText(unit.getContents().get(0).getTitle());
        title1.setTextColor(Color.parseColor(unit.getContents().get(0).getTitleColor()));
        msg1.setText(unit.getContents().get(0).getMessage());
        msg1.setTextColor(Color.parseColor(unit.getContents().get(0).getMessageColor()));
        Glide
                .with(this)
                .load(unit.getContents().get(0).getMedia())
//                .load("https://db7hsdc8829us.cloudfront.net/dist/1615456257/i/593e2d22c1d940beb2821fad3e2b0ccf.jpeg?v=1659719722")

                .into(img1);


        String title2 = unit.getContents().get(1).getTitle();
        String msg2 = unit.getContents().get(1).getMessage();
        String media2 = unit.getContents().get(1).getMedia();

        String Content = String.valueOf(unit.getContents());



        Toast.makeText(this,unitID+"  -  "+type+"  -  "+bgColor+"  -  "+title1, Toast.LENGTH_LONG).show();
        Log.e("Content Payload",Content);
    }
}