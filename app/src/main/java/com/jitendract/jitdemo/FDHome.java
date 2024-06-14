package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private TextView fdKnowMore;
    private MaterialCardView fdRates, fdSubmitCard;
    private Slider amountSeekBar, tenureSeekBar;
    private Spinner interestPayoutSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdhome);

        fdKnowMore = findViewById(R.id.fdrates_knowmore);
        fdRates = findViewById(R.id.fdratesimage);
        fdSubmitCard = findViewById(R.id.fdsubmit);

        fdKnowMore.setOnClickListener(v -> {
            switch (String.valueOf(fdKnowMore.getText())) {
                case "Know More":
                    fdKnowMore.setText("Hide");
                    fdRates.setVisibility(View.VISIBLE);
                    break;
                case "Hide":
                    fdKnowMore.setText("Know More");
                    fdRates.setVisibility(View.GONE);
                    break;
                default:
                    Log.e("PEException", "Something Went wrong");
            }
        });

        fdSubmitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FDHome.this, FDSummary.class);
                startActivity(intent);
            }
        });
    }
}
