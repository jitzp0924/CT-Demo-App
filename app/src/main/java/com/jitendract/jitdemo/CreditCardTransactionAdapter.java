package com.jitendract.jitdemo;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CreditCardTransactionAdapter
        extends RecyclerView.Adapter<CreditCardTransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;

    public CreditCardTransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cc_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        holder.icon.setText(String.valueOf(t.getInitial()));
        holder.icon.setBackgroundTintList(ColorStateList.valueOf(t.iconColor));
        holder.merchant.setText(t.merchant);
        holder.categoryDate.setText(t.category + " • " + t.date);

        String prefix = t.isCredit ? "+" : "-";
        holder.amount.setText(prefix + "₹" + fmt.format(t.amount));
        holder.amount.setTextColor(t.isCredit ? 0xFF2E7D32 : 0xFF9F3951);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon, merchant, categoryDate, amount;

        ViewHolder(View v) {
            super(v);
            icon         = v.findViewById(R.id.txn_icon);
            merchant     = v.findViewById(R.id.txn_merchant);
            categoryDate = v.findViewById(R.id.txn_category_date);
            amount       = v.findViewById(R.id.txn_amount);
        }
    }

    public static class Transaction {
        public final String merchant;
        public final String category;
        public final String date;
        public final int amount;
        public final boolean isCredit;
        public final int iconColor;

        public Transaction(String merchant, String category, String date,
                           int amount, boolean isCredit, int iconColor) {
            this.merchant  = merchant;
            this.category  = category;
            this.date      = date;
            this.amount    = amount;
            this.isCredit  = isCredit;
            this.iconColor = iconColor;
        }

        char getInitial() {
            return merchant.isEmpty() ? '?' : Character.toUpperCase(merchant.charAt(0));
        }
    }
}
