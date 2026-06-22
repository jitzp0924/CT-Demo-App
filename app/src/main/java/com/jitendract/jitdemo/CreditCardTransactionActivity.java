package com.jitendract.jitdemo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.chip.ChipGroup;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreditCardTransactionActivity extends AppCompatActivity {

    private CreditCardTransactionAdapter adapter;
    private List<CreditCardTransactionAdapter.Transaction> allTransactions;
    private CreditCard card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_transactions);

        int cardIndex = getIntent().getIntExtra("cardIndex", 0);
        List<CreditCard> cards = CreditCard.getAll(this);
        cardIndex = Math.min(cardIndex, cards.size() - 1);
        card = cards.get(cardIndex);
        allTransactions = card.transactions;

        Toolbar toolbar = findViewById(R.id.cc_txn_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(card.cardName + " Transactions");

        RecyclerView rv = findViewById(R.id.cc_all_transactions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new CreditCardTransactionAdapter(new ArrayList<>(allTransactions));
        rv.setAdapter(adapter);

        setupFilter();
        refreshSummary(allTransactions);
        trackEvent(cardIndex);
    }

    private void setupFilter() {
        ChipGroup chipGroup = findViewById(R.id.txn_filter_chips);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            List<CreditCardTransactionAdapter.Transaction> filtered = new ArrayList<>();
            for (CreditCardTransactionAdapter.Transaction t : allTransactions) {
                if      (id == R.id.chip_all)                   filtered.add(t);
                else if (id == R.id.chip_debit  && !t.isCredit) filtered.add(t);
                else if (id == R.id.chip_credit &&  t.isCredit) filtered.add(t);
            }
            adapter.setTransactions(filtered);
            refreshSummary(filtered);
        });
    }

    private void refreshSummary(List<CreditCardTransactionAdapter.Transaction> shown) {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        int debitTotal = 0;
        for (CreditCardTransactionAdapter.Transaction t : allTransactions) {
            if (!t.isCredit) debitTotal += t.amount;
        }
        ((TextView) findViewById(R.id.txn_this_month)).setText("₹" + fmt.format(debitTotal));
        ((TextView) findViewById(R.id.txn_count)).setText(String.valueOf(shown.size()));
    }

    private void trackEvent(int cardIndex) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(this);
        if (ct == null) return;
        HashMap<String, Object> evt = new HashMap<>();
        evt.put("Screen", "Credit Card Transactions");
        evt.put("Action", "View");
        evt.put("CardIndex", cardIndex);
        evt.put("CardName", card.cardName);
        ct.pushEvent("Credit Card", evt);
    }
}
