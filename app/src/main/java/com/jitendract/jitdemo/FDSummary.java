package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.Locale;

public class FDSummary extends AppCompatActivity {

    private TextView dropdownMoreDetails;
    private LinearLayout moreDetailsContent;
    private CheckBox termsCheckbox;
    private MaterialButton nextButton;

    // Hero card
    private TextView yieldText, depositAmountText, tenureText, roiText;
    // Detail card
    private TextView nomineeTxt;

    // Instance fields for back-press event tracking
    private String investmentAmount, tenureValue, rateOfInterest, nomineeName, maturityAmountStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdsummary);

        // Toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.fd_summary_toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bindViews();
        readIntentExtras();
        populateViews();

        FDEventTracker.track(this, FDEventTracker.SCREEN_REVIEW, FDEventTracker.ACTION_VIEW, intentPayload());

        setupAccordion();
        setupNextButton();
    }

    // ── View binding ──────────────────────────────────────────────────────────

    private void bindViews() {
        yieldText         = findViewById(R.id.yield);
        depositAmountText = findViewById(R.id.deposit_amount);
        tenureText        = findViewById(R.id.tenure);
        roiText           = findViewById(R.id.roi);
        nomineeTxt        = findViewById(R.id.nominee_txt);
        dropdownMoreDetails = findViewById(R.id.dropdown_more_details);
        moreDetailsContent  = findViewById(R.id.more_details_content);
        termsCheckbox       = findViewById(R.id.terms_checkbox);
        nextButton          = findViewById(R.id.next_button);
    }

    // ── Data from intent ──────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent i       = getIntent();
        investmentAmount  = i.getStringExtra("investmentAmount");
        tenureValue       = i.getStringExtra("tenure");
        rateOfInterest    = i.getStringExtra("rateOfInterest");
        nomineeName       = i.getStringExtra("nomineeName");
        // maturityAmount may already be computed by FDHome; fall back to computing it here
        maturityAmountStr = i.getStringExtra("maturityAmount");
        if (maturityAmountStr == null) {
            float rate = safeParseFloat(rateOfInterest);
            int   mat  = FDBookingActivity.calcMaturity(safeParseInt(investmentAmount), rate, safeParseInt(tenureValue));
            maturityAmountStr = String.valueOf(mat);
        }
    }

    // ── Populate hero card and detail rows ────────────────────────────────────

    private void populateViews() {
        // Hero card
        yieldText.setText(fmtCurrency(safeParseInt(maturityAmountStr)));
        depositAmountText.setText(fmtCurrency(safeParseInt(investmentAmount)));
        tenureText.setText(labelTenure(safeParseInt(tenureValue)));
        roiText.setText(rateOfInterest + "% p.a.");

        // Detail rows
        nomineeTxt.setText((nomineeName != null && !nomineeName.isEmpty()) ? nomineeName : "Not added");

        // Next button disabled until T&C is checked
        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f);
    }

    // ── Accordion (more details) ──────────────────────────────────────────────

    private void setupAccordion() {
        dropdownMoreDetails.setOnClickListener(v -> {
            boolean isOpen = moreDetailsContent.getVisibility() == View.VISIBLE;
            moreDetailsContent.setVisibility(isOpen ? View.GONE : View.VISIBLE);
            dropdownMoreDetails.setText(isOpen ? "▼" : "▲");
        });
    }

    // ── Next button ───────────────────────────────────────────────────────────

    private void setupNextButton() {
        termsCheckbox.setOnCheckedChangeListener((btn, checked) -> {
            nextButton.setEnabled(checked);
            nextButton.setAlpha(checked ? 1.0f : 0.5f);
        });

        nextButton.setOnClickListener(v -> {
            FDEventTracker.track(this, FDEventTracker.SCREEN_REVIEW, FDEventTracker.ACTION_CLICK,
                    new FDEventPayload.Builder()
                            .amount(investmentAmount).tenure(tenureValue)
                            .rate(rateOfInterest).nominee(nomineeName)
                            .maturity(maturityAmountStr).build());

            Intent next = new Intent(this, FDMpinActivity.class);
            next.putExtra("investmentAmount", investmentAmount);
            next.putExtra("tenure",           tenureValue);
            next.putExtra("rateOfInterest",   rateOfInterest);
            next.putExtra("nomineeName",      nomineeName);
            next.putExtra("maturityAmount",   maturityAmountStr);
            startActivity(next);
        });
    }

    // ── Back ──────────────────────────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        FDEventTracker.track(this, FDEventTracker.SCREEN_REVIEW, FDEventTracker.ACTION_BACK, intentPayload());
        super.onBackPressed();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private FDEventPayload intentPayload() {
        return new FDEventPayload.Builder()
                .amount(investmentAmount).tenure(tenureValue)
                .rate(rateOfInterest).nominee(nomineeName)
                .maturity(maturityAmountStr).build();
    }

    private static String fmtCurrency(int amount) {
        return "₹" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(amount);
    }

    private static String labelTenure(int months) {
        if (months < 12) return months + " months";
        int years = months / 12, rem = months % 12;
        if (rem == 0) return years + (years == 1 ? " year" : " years");
        return years + "Y " + rem + "M";
    }

    private static int safeParseInt(String raw) {
        try { return Integer.parseInt(raw != null ? raw.trim() : "0"); }
        catch (NumberFormatException e) { return 0; }
    }

    private static float safeParseFloat(String raw) {
        try { return Float.parseFloat(raw != null ? raw.replace("%", "").trim() : "5"); }
        catch (NumberFormatException e) { return 5.0f; }
    }
}
