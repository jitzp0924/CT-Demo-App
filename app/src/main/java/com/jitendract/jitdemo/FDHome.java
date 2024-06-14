package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

public class FDHome extends AppCompatActivity {

    private TextView fdKnowMore, investmentAmountDisplay, fdTenure, ratingText;
    private MaterialCardView fdRates, fdSubmitCard;
    private Slider amountSeekBar, tenureSeekBar;
    private TextInputEditText nomineeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdhome);

        fdKnowMore = findViewById(R.id.fdrates_knowmore);
        fdRates = findViewById(R.id.fdratesimage);
        fdSubmitCard = findViewById(R.id.fdsubmit);
        amountSeekBar = findViewById(R.id.fd_deposit_slider);
        tenureSeekBar = findViewById(R.id.fd_tenure_slider);
        investmentAmountDisplay = findViewById(R.id.investment_amount_display);
        fdTenure = findViewById(R.id.fd_tenure);
        ratingText = findViewById(R.id.ratingvalue);
        nomineeName = findViewById(R.id.nomineeName);

        // Set initial values and listeners
        amountSeekBar.setStepSize(500);
        amountSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            investmentAmountDisplay.setText(String.valueOf((int) value));
        });

        tenureSeekBar.setStepSize(1);
        tenureSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            fdTenure.setText(String.valueOf((int) value));
            updateRatingText((int) value);
        });

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
                intent.putExtra("investmentAmount", investmentAmountDisplay.getText().toString());
                intent.putExtra("tenure", fdTenure.getText().toString());
                intent.putExtra("rateOfInterest", ratingText.getText().toString());
                intent.putExtra("nomineeName", nomineeName.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void updateRatingText(int tenure) {
        String rate;
        if (tenure <= 6) {
            rate = "4.50%";
        } else if (tenure <= 18) {
            rate = "5%";
        } else if (tenure <= 36) {
            rate = "5.25%";
        } else {
            rate = "6%";
        }
        ratingText.setText(rate);
    }
}
