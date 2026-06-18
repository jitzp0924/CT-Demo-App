package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.util.Locale;

public class FDHome extends AppCompatActivity {

    private TextView fdKnowMore, investmentAmountDisplay, fdTenure, ratingText, liveMaturity;
    private MaterialCardView fdRates;
    private View fdSubmitCard; // declared as View — works for both CardView and MaterialButton
    private Slider amountSeekBar, tenureSeekBar;
    private TextInputEditText nomineeName;

    // Track current values so slider callbacks always have both
    private int currentAmount = 50_000;
    private int currentTenure = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdhome);

        // Toolbar back → tracked onBackPressed
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bindViews();
        setupSliders();
        setupKnowMore();
        setupContinueButton();
        refreshDisplays(); // seed all live views with defaults

        FDEventTracker.track(this, FDEventTracker.SCREEN_DETAILS, FDEventTracker.ACTION_VIEW, null);
    }

    // ── View binding ──────────────────────────────────────────────────────────

    private void bindViews() {
        fdKnowMore             = findViewById(R.id.fdrates_knowmore);
        fdRates                = findViewById(R.id.fdratesimage);
        fdSubmitCard           = findViewById(R.id.fdsubmit);
        amountSeekBar          = findViewById(R.id.fd_deposit_slider);
        tenureSeekBar          = findViewById(R.id.fd_tenure_slider);
        investmentAmountDisplay = findViewById(R.id.investment_amount_display);
        fdTenure               = findViewById(R.id.fd_tenure);
        ratingText             = findViewById(R.id.ratingvalue);
        nomineeName            = findViewById(R.id.nomineeName);
        liveMaturity           = findViewById(R.id.fd_live_maturity);
    }

    // ── Sliders ───────────────────────────────────────────────────────────────

    private void setupSliders() {
        amountSeekBar.setStepSize(500);
        amountSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            currentAmount = (int) value;
            refreshDisplays();
        });

        tenureSeekBar.setStepSize(1);
        tenureSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            currentTenure = (int) value;
            refreshDisplays();
        });
    }

    /** Updates all live-display TextViews whenever amount or tenure changes. */
    private void refreshDisplays() {
        float rate     = FDRateHelper.getRate(this, currentTenure);
        int   maturity = FDBookingActivity.calcMaturity(currentAmount, rate, currentTenure);

        investmentAmountDisplay.setText(fmtCurrency(currentAmount));
        fdTenure.setText(labelTenure(currentTenure));
        ratingText.setText(String.format(Locale.getDefault(), "%.2f%% p.a.", rate));
        liveMaturity.setText(fmtCurrency(maturity));
    }

    // ── Know More toggle ──────────────────────────────────────────────────────

    private void setupKnowMore() {
        fdKnowMore.setOnClickListener(v -> {
            boolean isShowing = fdRates.getVisibility() == View.VISIBLE;
            fdRates.setVisibility(isShowing ? View.GONE : View.VISIBLE);
            fdKnowMore.setText(isShowing ? "Rate Card ›" : "Hide ✕");
        });
    }

    // ── Continue ──────────────────────────────────────────────────────────────

    private void setupContinueButton() {
        fdSubmitCard.setOnClickListener(v -> {
            FDEventTracker.track(this, FDEventTracker.SCREEN_DETAILS,
                    FDEventTracker.ACTION_CLICK, snapshot());
            float rate     = FDRateHelper.getRate(this, currentTenure);
            int   maturity = FDBookingActivity.calcMaturity(currentAmount, rate, currentTenure);

            Intent intent = new Intent(this, FDSummary.class);
            intent.putExtra("investmentAmount", String.valueOf(currentAmount));
            intent.putExtra("tenure",           String.valueOf(currentTenure));
            intent.putExtra("rateOfInterest",   String.format(Locale.getDefault(), "%.2f", rate));
            intent.putExtra("nomineeName",      nomineeText());
            intent.putExtra("maturityAmount",   String.valueOf(maturity));
            startActivity(intent);
        });
    }

    // ── Back ──────────────────────────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        FDEventTracker.track(this, FDEventTracker.SCREEN_DETAILS, FDEventTracker.ACTION_BACK, snapshot());
        super.onBackPressed();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private FDEventPayload snapshot() {
        float rate     = FDRateHelper.getRate(this, currentTenure);
        int   maturity = FDBookingActivity.calcMaturity(currentAmount, rate, currentTenure);
        return new FDEventPayload.Builder()
                .amount(String.valueOf(currentAmount))
                .tenure(String.valueOf(currentTenure))
                .rate(String.format(Locale.getDefault(), "%.2f", rate))
                .nominee(nomineeText())
                .maturity(String.valueOf(maturity))
                .build();
    }

    private String nomineeText() {
        return nomineeName.getText() != null ? nomineeName.getText().toString().trim() : "";
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
}
