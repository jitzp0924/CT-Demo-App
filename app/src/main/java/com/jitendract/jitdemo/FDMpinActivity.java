package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shared MPIN screen — used by both FD journey variants.
 * Receives FD extras from either FDBookingActivity (V1) or FDSummary (V2).
 */
public class FDMpinActivity extends AppCompatActivity {

    private static final int PIN_LENGTH = 6;

    private final StringBuilder pin = new StringBuilder();
    private final List<ImageView> pinDots = new ArrayList<>();
    private MaterialButton confirmButton;

    // FD data passed in from previous screen
    private String investmentAmount, tenure, rateOfInterest, nomineeName, maturityAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_mpin);

        // Receive extras
        investmentAmount = getIntent().getStringExtra("investmentAmount");
        tenure           = getIntent().getStringExtra("tenure");
        rateOfInterest   = getIntent().getStringExtra("rateOfInterest");
        nomineeName      = getIntent().getStringExtra("nomineeName");
        maturityAmount   = getIntent().getStringExtra("maturityAmount");

        // Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.fd_mpin_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Booking summary strip
        TextView summaryAmount = findViewById(R.id.mpin_summary_amount);
        TextView summaryDetail = findViewById(R.id.mpin_summary_detail);
        summaryAmount.setText(fmtCurrency(investmentAmount));
        summaryDetail.setText(tenure + " months · " + rateOfInterest + "% p.a.");

        // PIN dots
        pinDots.add(findViewById(R.id.pin_dot_1));
        pinDots.add(findViewById(R.id.pin_dot_2));
        pinDots.add(findViewById(R.id.pin_dot_3));
        pinDots.add(findViewById(R.id.pin_dot_4));
        pinDots.add(findViewById(R.id.pin_dot_5));
        pinDots.add(findViewById(R.id.pin_dot_6));

        confirmButton = findViewById(R.id.btn_mpin_confirm);
        confirmButton.setEnabled(false);
        confirmButton.setAlpha(0.5f);
        confirmButton.setOnClickListener(v -> launchSuccess());

        setupNumpad();

        FDEventTracker.track(this, FDEventTracker.SCREEN_MPIN, FDEventTracker.ACTION_VIEW, intentPayload());
    }

    @Override
    public void onBackPressed() {
        FDEventTracker.track(this, FDEventTracker.SCREEN_MPIN, FDEventTracker.ACTION_BACK, intentPayload());
        finish();
    }

    // ── Numpad wiring ─────────────────────────────────────────────────────────

    private void setupNumpad() {
        int[] keyIds = {
            R.id.key_1, R.id.key_2, R.id.key_3,
            R.id.key_4, R.id.key_5, R.id.key_6,
            R.id.key_7, R.id.key_8, R.id.key_9,
            R.id.key_0
        };
        String[] digits = {"1","2","3","4","5","6","7","8","9","0"};

        for (int i = 0; i < keyIds.length; i++) {
            final String digit = digits[i];
            findViewById(keyIds[i]).setOnClickListener(v -> appendDigit(digit));
        }
        findViewById(R.id.key_backspace).setOnClickListener(v -> deleteDigit());
    }

    private void appendDigit(String digit) {
        if (pin.length() >= PIN_LENGTH) return;
        pin.append(digit);
        pinDots.get(pin.length() - 1).setBackgroundResource(R.drawable.pin_dot_filled);
        if (pin.length() == PIN_LENGTH) {
            confirmButton.setEnabled(true);
            confirmButton.setAlpha(1.0f);
        }
    }

    private void deleteDigit() {
        if (pin.length() == 0) return;
        if (pin.length() == PIN_LENGTH) {
            confirmButton.setEnabled(false);
            confirmButton.setAlpha(0.5f);
        }
        pinDots.get(pin.length() - 1).setBackgroundResource(R.drawable.pin_dot_empty);
        pin.deleteCharAt(pin.length() - 1);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void launchSuccess() {
        FDEventTracker.track(this, FDEventTracker.SCREEN_MPIN, FDEventTracker.ACTION_CLICK, intentPayload());
        Intent i = new Intent(this, FDSuccessActivity.class);
        i.putExtra("investmentAmount", investmentAmount);
        i.putExtra("tenure",           tenure);
        i.putExtra("rateOfInterest",   rateOfInterest);
        i.putExtra("nomineeName",      nomineeName);
        i.putExtra("maturityAmount",   maturityAmount);
        startActivity(i);
        finish(); // MPIN screen should not be in back-stack after success
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Builds a payload from the intent extras — all fields are always set on this screen. */
    private FDEventPayload intentPayload() {
        return new FDEventPayload.Builder()
                .amount(investmentAmount)
                .tenure(tenure)
                .rate(rateOfInterest)
                .nominee(nomineeName)
                .maturity(maturityAmount)
                .build();
    }

    private static String fmtCurrency(String raw) {
        try {
            int amount = Integer.parseInt(raw);
            return "₹" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(amount);
        } catch (NumberFormatException e) {
            return raw != null ? raw : "—";
        }
    }
}
