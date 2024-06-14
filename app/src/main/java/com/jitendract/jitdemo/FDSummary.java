package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class FDSummary extends AppCompatActivity {

    private TextView moreDetails;
    private TextView dropdownMoreDetails;
    private LinearLayout moreDetailsContent;
    private CheckBox termsCheckbox;
    private MaterialCardView nextButton;

    private TextView depositAmount;
    private TextView tenure;
    private TextView roi;
    private TextView nomineeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdsummary);

        moreDetails = findViewById(R.id.more_details);
        dropdownMoreDetails = findViewById(R.id.dropdown_more_details);
        moreDetailsContent = findViewById(R.id.more_details_content);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        nextButton = findViewById(R.id.next_button);

        depositAmount = findViewById(R.id.deposit_amount);
        tenure = findViewById(R.id.tenure);
        roi = findViewById(R.id.roi);
        nomineeTxt = findViewById(R.id.nominee_txt);

        // Retrieve data from intent
        Intent intent = getIntent();
        String investmentAmount = intent.getStringExtra("investmentAmount");
        String tenureValue = intent.getStringExtra("tenure");
        String rateOfInterest = intent.getStringExtra("rateOfInterest");
        String nomineeName = intent.getStringExtra("nomineeName");

        // Set data to TextViews
        depositAmount.setText(investmentAmount);
        tenure.setText(tenureValue);
        roi.setText(rateOfInterest);
        nomineeTxt.setText(nomineeName);

        dropdownMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreDetailsContent.getVisibility() == View.GONE) {
                    moreDetailsContent.setVisibility(View.VISIBLE);
                    dropdownMoreDetails.setText("▲");
                } else {
                    moreDetailsContent.setVisibility(View.GONE);
                    dropdownMoreDetails.setText("▼");
                }
            }
        });

        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> nextButton.setEnabled(isChecked));
    }
}
