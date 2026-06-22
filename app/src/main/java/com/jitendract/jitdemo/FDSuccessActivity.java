package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Shared success screen — used by both FD journey variants.
 * Receives FD extras forwarded from FDMpinActivity.
 */
public class FDSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_success);

        String investmentAmount = getIntent().getStringExtra("investmentAmount");
        String tenure           = getIntent().getStringExtra("tenure");
        String rateOfInterest   = getIntent().getStringExtra("rateOfInterest");
        String maturityAmount   = getIntent().getStringExtra("maturityAmount");

        // Reference number: "FD" + last 8 digits of current time
        String refNumber = "FD" + (System.currentTimeMillis() % 100_000_000L);

        FDEventTracker.track(this, FDEventTracker.SCREEN_SUCCESS, FDEventTracker.ACTION_SUCCESS,
                new FDEventPayload.Builder()
                        .amount(investmentAmount)
                        .tenure(tenure)
                        .rate(rateOfInterest)
                        .maturity(maturityAmount)
                        .build());

        ((TextView) findViewById(R.id.success_ref_number)).setText("Ref: " + refNumber);
        ((TextView) findViewById(R.id.success_amount)).setText(fmtCurrency(investmentAmount));
        ((TextView) findViewById(R.id.success_tenure)).setText(labelTenure(tenure));
        ((TextView) findViewById(R.id.success_rate)).setText(rateOfInterest + "% p.a.");
        ((TextView) findViewById(R.id.success_maturity)).setText(fmtCurrency(maturityAmount));

        ((MaterialButton) findViewById(R.id.btn_view_fd)).setOnClickListener(v ->
                Toast.makeText(this, "FD details — coming soon", Toast.LENGTH_SHORT).show());

        ((MaterialButton) findViewById(R.id.btn_go_home)).setOnClickListener(v -> {
            Intent i = new Intent(this, HomeScreenV2.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String fmtCurrency(String raw) {
        try {
            int amount = Integer.parseInt(raw);
            return "₹" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(amount);
        } catch (NumberFormatException e) {
            return raw != null ? raw : "—";
        }
    }

    private static String labelTenure(String raw) {
        try {
            int months = Integer.parseInt(raw);
            if (months < 12) return months + " months";
            int years = months / 12, rem = months % 12;
            if (rem == 0) return years + (years == 1 ? " year" : " years");
            return years + "Y " + rem + "M";
        } catch (NumberFormatException e) {
            return raw != null ? raw : "—";
        }
    }
}
