package com.jitendract.jitdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FDSummary extends AppCompatActivity {
    private TextView dropdownMoreDetails;
    private LinearLayout moreDetailsContent;
    private CheckBox termsCheckbox;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdsummary);

        moreDetails = findViewById(R.id.more_details);
        dropdownMoreDetails = findViewById(R.id.dropdown_more_details);
        moreDetailsContent = findViewById(R.id.more_details_content);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        nextButton = findViewById(R.id.next_button);

        // Change the listener to dropdownMoreDetails instead of moreDetails
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

        termsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nextButton.setEnabled(isChecked);
            }
        });
    }
}
