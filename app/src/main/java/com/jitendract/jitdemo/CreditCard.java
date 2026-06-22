package com.jitendract.jitdemo;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class CreditCard {

    public final String cardName;
    public final String cardNumber;
    public final String holderName;
    public final String expiry;
    public final int networkLogoRes;
    public final int gradientDrawable;
    public final int availableCredit;
    public final int totalLimit;
    public final int usedAmount;
    public final int minimumDue;
    public final String dueDate;
    public final List<CreditCardTransactionAdapter.Transaction> transactions;

    public CreditCard(String cardName, String cardNumber, String holderName,
                      String expiry, int networkLogoRes, int gradientDrawable,
                      int availableCredit, int totalLimit, int usedAmount,
                      int minimumDue, String dueDate,
                      List<CreditCardTransactionAdapter.Transaction> transactions) {
        this.cardName        = cardName;
        this.cardNumber      = cardNumber;
        this.holderName      = holderName;
        this.expiry          = expiry;
        this.networkLogoRes  = networkLogoRes;
        this.gradientDrawable = gradientDrawable;
        this.availableCredit = availableCredit;
        this.totalLimit      = totalLimit;
        this.usedAmount      = usedAmount;
        this.minimumDue      = minimumDue;
        this.dueDate         = dueDate;
        this.transactions    = transactions;
    }

    public int usagePercent() {
        if (totalLimit == 0) return 0;
        return (int) ((usedAmount * 100.0f) / totalLimit);
    }

    public String maskedNumber() {
        String digits = cardNumber.replaceAll("\\s+", "");
        if (digits.length() < 4) return cardNumber;
        return "•••• •••• •••• "
                + digits.substring(digits.length() - 4);
    }

    /** Builds the full card list, pulling the holder name from shared prefs. */
    public static List<CreditCard> getAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String name = prefs.getString("Identity", "");
        if (name.isEmpty() || name.equals("default")) name = "Rahul Sharma";
        return buildAll(name);
    }

    static List<CreditCard> buildAll(String holderName) {
        List<CreditCard> list = new ArrayList<>();

        // ── Card 1: Signature (VISA) ──────────────────────────────────────────
        List<CreditCardTransactionAdapter.Transaction> t1 = new ArrayList<>();
        t1.add(new CreditCardTransactionAdapter.Transaction("Amazon",      "Shopping",      "17 Jun 2026", 2499, false, 0xFF3F51B5));
        t1.add(new CreditCardTransactionAdapter.Transaction("Zomato",      "Food & Dining", "16 Jun 2026",  387, false, 0xFFE53935));
        t1.add(new CreditCardTransactionAdapter.Transaction("Netflix",     "Entertainment", "14 Jun 2026",  649, false, 0xFF212121));
        t1.add(new CreditCardTransactionAdapter.Transaction("HPCL Petrol", "Fuel",          "13 Jun 2026", 3000, false, 0xFF43A047));
        t1.add(new CreditCardTransactionAdapter.Transaction("Flipkart",    "Shopping",      "10 Jun 2026", 8999, false, 0xFF1976D2));
        t1.add(new CreditCardTransactionAdapter.Transaction("Cashback",    "Reward Credit", "01 Jun 2026",  450,  true, 0xFF9F3951));
        list.add(new CreditCard(
                "Signature", "4523  8191  6671  3190", holderName,
                "09/29", R.drawable.ic_network_visa, R.drawable.bg_credit_card,
                254720, 300000, 45280, 2264, "05 Jul 2026", t1));

        // ── Card 2: Platinum (Mastercard) ─────────────────────────────────────
        List<CreditCardTransactionAdapter.Transaction> t2 = new ArrayList<>();
        t2.add(new CreditCardTransactionAdapter.Transaction("Apple Store", "Shopping",      "15 Jun 2026", 15999, false, 0xFF212121));
        t2.add(new CreditCardTransactionAdapter.Transaction("Uber",        "Transport",     "14 Jun 2026",   234, false, 0xFF37474F));
        t2.add(new CreditCardTransactionAdapter.Transaction("BigBasket",   "Groceries",     "12 Jun 2026",  2187, false, 0xFF558B2F));
        t2.add(new CreditCardTransactionAdapter.Transaction("Swiggy",      "Food & Dining", "10 Jun 2026",   520, false, 0xFFFF6F00));
        t2.add(new CreditCardTransactionAdapter.Transaction("Cashback",    "Reward Credit", "01 Jun 2026",   350,  true, 0xFF1565C0));
        list.add(new CreditCard(
                "Platinum", "5412  7834  9021  4567", holderName,
                "03/28", R.drawable.ic_network_mastercard, R.drawable.bg_credit_card_blue,
                82500, 100000, 17500, 875, "05 Jul 2026", t2));

        // ── Card 3: Rewards (RuPay) ───────────────────────────────────────────
        List<CreditCardTransactionAdapter.Transaction> t3 = new ArrayList<>();
        t3.add(new CreditCardTransactionAdapter.Transaction("Myntra",      "Shopping",      "16 Jun 2026", 1299, false, 0xFFE91E63));
        t3.add(new CreditCardTransactionAdapter.Transaction("Starbucks",   "Food & Dining", "15 Jun 2026",  480, false, 0xFF1B5E20));
        t3.add(new CreditCardTransactionAdapter.Transaction("BookMyShow",  "Entertainment", "11 Jun 2026",  840, false, 0xFF6A1B9A));
        t3.add(new CreditCardTransactionAdapter.Transaction("Cashback",    "Reward Credit", "01 Jun 2026",  124,  true, 0xFF00695C));
        list.add(new CreditCard(
                "Rewards", "6070  2811  5523  9034", holderName,
                "11/27", R.drawable.ic_network_rupay, R.drawable.bg_credit_card_teal,
                23800, 25000, 1200, 120, "05 Jul 2026", t3));

        return list;
    }
}
