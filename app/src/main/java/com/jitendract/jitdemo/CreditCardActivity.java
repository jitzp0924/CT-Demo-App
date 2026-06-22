package com.jitendract.jitdemo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.clevertap.android.sdk.CleverTapAPI;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreditCardActivity extends AppCompatActivity {

    private RecyclerView cardPager;
    private LinearLayout dotsLayout;
    private TextView availableCreditText, totalLimitText, usedLimitText, dueAmountText, dueDateText;
    private ProgressBar limitProgress;
    private CreditCardTransactionAdapter transactionAdapter;
    private List<CreditCard> cards;
    private int currentCardIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        Toolbar toolbar = findViewById(R.id.cc_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        cards = CreditCard.getAll(this);

        cardPager          = findViewById(R.id.cc_card_pager);
        dotsLayout         = findViewById(R.id.cc_card_dots);
        availableCreditText = findViewById(R.id.cc_available_credit);
        totalLimitText     = findViewById(R.id.cc_total_limit_text);
        usedLimitText      = findViewById(R.id.cc_used_limit_text);
        limitProgress      = findViewById(R.id.cc_limit_progress);
        dueAmountText      = findViewById(R.id.cc_due_amount);
        dueDateText        = findViewById(R.id.cc_due_date);

        setupCardPager();
        setupDots();
        setupRecentTransactions();
        setupQuickActions();
        updateCardDetails(0);
        trackEvent("View", 0);
    }

    // ── Card carousel ─────────────────────────────────────────────────────────

    private void setupCardPager() {
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cardPager.setLayoutManager(lm);
        cardPager.setAdapter(new CreditCardPagerAdapter(cards));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(cardPager);

        cardPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) return;
                View snapView = snapHelper.findSnapView(rv.getLayoutManager());
                if (snapView == null) return;
                int pos = rv.getLayoutManager().getPosition(snapView);
                if (pos >= 0 && pos != currentCardIndex) {
                    currentCardIndex = pos;
                    updateDots(pos);
                    updateCardDetails(pos);
                    updateRecentTransactions(pos);
                    trackEvent("Swipe", pos);
                }
            }
        });
    }

    // ── Dot indicators ────────────────────────────────────────────────────────

    private void setupDots() {
        dotsLayout.removeAllViews();
        int sizePx  = dp(8);
        int gapPx   = dp(5);
        for (int i = 0; i < cards.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMargins(gapPx, 0, gapPx, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.bg_merchant_icon);
            dot.setBackgroundTintList(ColorStateList.valueOf(
                    i == 0 ? 0xFF9F3951 : 0xFFCCCCCC));
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int selected) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            dotsLayout.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(
                    i == selected ? 0xFF9F3951 : 0xFFCCCCCC));
        }
    }

    // ── Card detail panel (limit / due) ───────────────────────────────────────

    private void updateCardDetails(int index) {
        CreditCard card = cards.get(index);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        availableCreditText.setText("₹" + fmt.format(card.availableCredit));
        totalLimitText.setText("₹" + fmt.format(card.totalLimit));
        limitProgress.setProgress(card.usagePercent());
        usedLimitText.setText("₹" + fmt.format(card.usedAmount)
                + " used of ₹" + fmt.format(card.totalLimit) + " total limit");
        dueAmountText.setText("₹" + fmt.format(card.minimumDue));
        dueDateText.setText(card.dueDate);
    }

    // ── Recent transactions ───────────────────────────────────────────────────

    private void setupRecentTransactions() {
        RecyclerView rv = findViewById(R.id.cc_recent_transactions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(false);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        List<CreditCardTransactionAdapter.Transaction> first =
                first4(cards.get(0).transactions);
        transactionAdapter = new CreditCardTransactionAdapter(first);
        rv.setAdapter(transactionAdapter);

        findViewById(R.id.cc_view_all_txns).setOnClickListener(v -> {
            trackEvent("View All Transactions", currentCardIndex);
            startActivity(txnIntent());
        });
    }

    private void updateRecentTransactions(int index) {
        transactionAdapter.setTransactions(first4(cards.get(index).transactions));
    }

    // ── Quick actions ─────────────────────────────────────────────────────────

    private void setupQuickActions() {
        findViewById(R.id.cc_action_pay).setOnClickListener(v -> {
            trackEvent("Pay Bill", currentCardIndex);
            Toast.makeText(this, "Pay Bill — coming soon", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.cc_action_statement).setOnClickListener(v -> {
            trackEvent("Statement", currentCardIndex);
            Toast.makeText(this, "Statements — coming soon", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.cc_action_transactions).setOnClickListener(v -> {
            trackEvent("Transactions", currentCardIndex);
            startActivity(txnIntent());
        });
        findViewById(R.id.cc_action_control).setOnClickListener(v -> {
            trackEvent("Card Controls", currentCardIndex);
            Intent i = new Intent(this, CreditCardControlCenterActivity.class);
            i.putExtra("cardIndex", currentCardIndex);
            startActivity(i);
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Intent txnIntent() {
        Intent i = new Intent(this, CreditCardTransactionActivity.class);
        i.putExtra("cardIndex", currentCardIndex);
        return i;
    }

    private static List<CreditCardTransactionAdapter.Transaction> first4(
            List<CreditCardTransactionAdapter.Transaction> src) {
        return src.subList(0, Math.min(4, src.size()));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void trackEvent(String action, int cardIndex) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(this);
        if (ct == null) return;
        HashMap<String, Object> evt = new HashMap<>();
        evt.put("Screen", "Credit Card");
        evt.put("Action", action);
        evt.put("CardIndex", cardIndex);
        evt.put("CardName", cards.get(cardIndex).cardName);
        ct.pushEvent("Credit Card", evt);
    }
}
