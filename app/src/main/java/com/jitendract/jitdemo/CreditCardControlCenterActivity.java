package com.jitendract.jitdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.clevertap.android.sdk.CleverTapAPI;
import java.util.HashMap;
import java.util.List;

public class CreditCardControlCenterActivity extends AppCompatActivity {

    private static final String CC_PREFS = "cc_controls";
    private SharedPreferences prefs;
    private CreditCard card;
    private int cardIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_control_center);

        cardIndex = getIntent().getIntExtra("cardIndex", 0);
        List<CreditCard> cards = CreditCard.getAll(this);
        cardIndex = Math.min(cardIndex, cards.size() - 1);
        card = cards.get(cardIndex);

        Toolbar toolbar = findViewById(R.id.cc_control_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Update the mini card header to reflect the selected card
        TextView cardNameView = findViewById(R.id.cc_control_card_name);
        TextView cardNumberView = findViewById(R.id.cc_control_card_number);
        if (cardNameView != null) cardNameView.setText("CTJ Bank " + card.cardName + " Card");
        if (cardNumberView != null) cardNumberView.setText(card.maskedNumber());

        // Per-card toggle prefs key prefix (e.g. "0_lock_card", "1_online_txn")
        String prefix = cardIndex + "_";
        prefs = getSharedPreferences(CC_PREFS, MODE_PRIVATE);

        bindToggle(R.id.toggle_lock_card,   prefix + "lock_card",   false, "Lock Card");
        bindToggle(R.id.toggle_online_txn,  prefix + "online_txn",  true,  "Online Transactions");
        bindToggle(R.id.toggle_intl,        prefix + "intl",        false, "International Use");
        bindToggle(R.id.toggle_contactless, prefix + "contactless", true,  "Contactless Pay");
        bindToggle(R.id.toggle_atm,         prefix + "atm",         true,  "ATM Withdrawals");

        findViewById(R.id.ctrl_report_lost).setOnClickListener(v -> {
            trackEvent("Report Lost", "Tap");
            Toast.makeText(this,
                    "Card flagged. Our team will contact you shortly.",
                    Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.ctrl_block_card).setOnClickListener(v -> {
            trackEvent("Block Card", "Tap");
            confirmBlockCard();
        });

        trackEvent("View", "Screen");
    }

    private void bindToggle(int switchId, String prefKey, boolean defaultVal, String label) {
        SwitchCompat sw = findViewById(switchId);
        sw.setChecked(prefs.getBoolean(prefKey, defaultVal));
        sw.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(prefKey, checked).apply();
            trackEvent(label, checked ? "Enabled" : "Disabled");
        });
    }

    private void confirmBlockCard() {
        new AlertDialog.Builder(this)
                .setTitle("Block " + card.cardName + " Card Permanently?")
                .setMessage("This action cannot be undone. Your card ending in "
                        + card.maskedNumber().substring(card.maskedNumber().length() - 4)
                        + " will be blocked and a replacement dispatched within 5–7 working days.")
                .setPositiveButton("Block Card", (d, w) -> {
                    trackEvent("Block Card", "Confirmed");
                    Toast.makeText(this,
                            "Card blocked. Replacement dispatched in 5–7 working days.",
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void trackEvent(String label, String action) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(this);
        if (ct == null) return;
        HashMap<String, Object> evt = new HashMap<>();
        evt.put("Screen", "Credit Card Controls");
        evt.put("Label", label);
        evt.put("Action", action);
        evt.put("CardIndex", cardIndex);
        evt.put("CardName", card.cardName);
        ct.pushEvent("Credit Card", evt);
    }
}
