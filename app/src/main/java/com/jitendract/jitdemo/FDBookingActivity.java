package com.jitendract.jitdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * V1 FD journey: single-screen booking with chip-based amount/tenure selection.
 * Calculates maturity amount live and proceeds to FDMpinActivity on confirmation.
 */
public class FDBookingActivity extends AppCompatActivity {

    private int selectedAmount = 50_000;
    private int selectedTenure = 12; // months

    private TextView maturityAmountText, heroPrincipalText, heroRateText, heroTenureText;
    private ChipGroup amountChips, tenureChips;
    private TextInputLayout customAmountLayout;
    private TextInputEditText customAmountInput, nomineeInput;
    private CheckBox termsCheckbox;
    private MaterialButton bookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_booking);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.fd_booking_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> trackAndFinish());

        bindViews();
        setupAmountChips();
        setupTenureChips();
        setupBookButton();
        updateHeroCard(); // show defaults on entry

        FDEventTracker.track(this, FDEventTracker.SCREEN_BOOKING, FDEventTracker.ACTION_VIEW, null);
    }

    @Override
    public void onBackPressed() {
        trackAndFinish();
    }

    /** Sends a BACK event with whatever the form state is, then closes. */
    private void trackAndFinish() {
        FDEventTracker.track(this, FDEventTracker.SCREEN_BOOKING, FDEventTracker.ACTION_BACK, snapshot());
        finish();
    }

    /** Captures the current form state — nominee is null if not filled. */
    private FDEventPayload snapshot() {
        float rate     = rateForTenure(this, selectedTenure);
        int   maturity = calcMaturity(selectedAmount, rate, selectedTenure);
        String nominee = nomineeInput.getText() != null ? nomineeInput.getText().toString() : "";
        return new FDEventPayload.Builder()
                .amount(String.valueOf(selectedAmount))
                .tenure(String.valueOf(selectedTenure))
                .rate(String.format(Locale.getDefault(), "%.2f", rate))
                .nominee(nominee)         // FDEventPayload.Builder strips empty → null
                .maturity(String.valueOf(maturity))
                .build();
    }

    private void bindViews() {
        maturityAmountText = findViewById(R.id.fd_maturity_amount);
        heroPrincipalText  = findViewById(R.id.fd_hero_principal);
        heroRateText       = findViewById(R.id.fd_hero_rate);
        heroTenureText     = findViewById(R.id.fd_hero_tenure);
        amountChips        = findViewById(R.id.fd_amount_chips);
        tenureChips        = findViewById(R.id.fd_tenure_chips);
        customAmountLayout = findViewById(R.id.fd_custom_amount_layout);
        customAmountInput  = findViewById(R.id.fd_custom_amount_input);
        nomineeInput       = findViewById(R.id.fd_nominee_input);
        termsCheckbox      = findViewById(R.id.fd_terms_checkbox);
        bookButton         = findViewById(R.id.fd_book_button);
    }

    // ── Amount chip selection ─────────────────────────────────────────────────

    private void setupAmountChips() {
        amountChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chip_10k)          { selectedAmount = 10_000;  hideCustomAmount(); }
            else if (id == R.id.chip_25k)          { selectedAmount = 25_000;  hideCustomAmount(); }
            else if (id == R.id.chip_50k)          { selectedAmount = 50_000;  hideCustomAmount(); }
            else if (id == R.id.chip_1l)           { selectedAmount = 1_00_000; hideCustomAmount(); }
            else if (id == R.id.chip_custom_amount) { showCustomAmount(); return; }
            updateHeroCard();
        });

        customAmountInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    if (val >= 5000) { selectedAmount = val; updateHeroCard(); }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void showCustomAmount() {
        customAmountLayout.setVisibility(View.VISIBLE);
        customAmountInput.requestFocus();
    }

    private void hideCustomAmount() {
        customAmountLayout.setVisibility(View.GONE);
    }

    // ── Tenure chip selection ─────────────────────────────────────────────────

    private void setupTenureChips() {
        tenureChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chip_6m)  selectedTenure = 6;
            else if (id == R.id.chip_12m) selectedTenure = 12;
            else if (id == R.id.chip_24m) selectedTenure = 24;
            else if (id == R.id.chip_36m) selectedTenure = 36;
            else if (id == R.id.chip_60m) selectedTenure = 60;
            updateHeroCard();
        });
    }

    // ── Hero card live update ─────────────────────────────────────────────────

    private void updateHeroCard() {
        float rate     = rateForTenure(this, selectedTenure);
        int   maturity = calcMaturity(selectedAmount, rate, selectedTenure);

        maturityAmountText.setText(fmt(maturity));
        heroPrincipalText.setText(fmt(selectedAmount));
        heroRateText.setText(String.format(Locale.getDefault(), "%.2f%% p.a.", rate));
        heroTenureText.setText(labelTenure(selectedTenure));
    }

    // ── Book button ───────────────────────────────────────────────────────────

    private void setupBookButton() {
        bookButton.setAlpha(0.5f);
        termsCheckbox.setOnCheckedChangeListener((btn, checked) -> {
            bookButton.setEnabled(checked);
            bookButton.setAlpha(checked ? 1.0f : 0.5f);
        });
        bookButton.setOnClickListener(v -> launchMpin());
    }

    private void launchMpin() {
        float rate     = rateForTenure(this, selectedTenure);
        int   maturity = calcMaturity(selectedAmount, rate, selectedTenure);
        String nominee = nomineeInput.getText() != null ? nomineeInput.getText().toString().trim() : "";

        FDEventTracker.track(this, FDEventTracker.SCREEN_BOOKING, FDEventTracker.ACTION_CLICK, snapshot());

        Intent i = new Intent(this, FDMpinActivity.class);
        i.putExtra("investmentAmount", String.valueOf(selectedAmount));
        i.putExtra("tenure",           String.valueOf(selectedTenure));
        i.putExtra("rateOfInterest",   String.format(Locale.getDefault(), "%.2f", rate));
        i.putExtra("nomineeName",      nominee);
        i.putExtra("maturityAmount",   String.valueOf(maturity));
        startActivity(i);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the applicable interest rate for the given tenure via PE config. */
    static float rateForTenure(Context ctx, int tenureMonths) {
        return FDRateHelper.getRate(ctx, tenureMonths);
    }

    /** Simple interest: M = P + (P × R × T) / (100 × 12) */
    static int calcMaturity(int principal, float annualRate, int tenureMonths) {
        return (int) (principal + (principal * annualRate * tenureMonths) / (100.0 * 12));
    }

    private static String fmt(int amount) {
        return "₹" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(amount);
    }

    private static String labelTenure(int months) {
        if (months < 12) return months + " months";
        int years = months / 12, rem = months % 12;
        if (rem == 0) return years + (years == 1 ? " year" : " years");
        return years + "Y " + rem + "M";
    }
}
