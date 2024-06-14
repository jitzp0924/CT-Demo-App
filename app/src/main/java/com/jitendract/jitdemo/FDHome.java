package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

public class FDHome extends AppCompatActivity {

    private TextView fdKnowMore ;
    private MaterialCardView fdRates;
    private Slider amountSeekBar, tenureSeekBar;
    private Spinner interestPayoutSpinner;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdhome);

        fdKnowMore = findViewById(R.id.fdrates_knowmore);
        fdRates = findViewById(R.id.fdratesimage);


        fdKnowMore.setOnClickListener( v-> {

            switch (String.valueOf(fdKnowMore.getText())){
                case "Know More":
                    fdKnowMore.setText("Hide");
                    fdRates.setVisibility(View.VISIBLE);
                    break;
                case "Hide":
                    fdKnowMore.setText("Know More");
                    fdRates.setVisibility(View.GONE);
                    break;
                default:
                    Log.e("PEException","Something Went wrong");
            }



        });
    }

}