package com.jitendract.jitdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CreditCardPagerAdapter extends RecyclerView.Adapter<CreditCardPagerAdapter.CardViewHolder> {

    private final List<CreditCard> cards;

    public CreditCardPagerAdapter(List<CreditCard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit_card_swipe, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CreditCard card = cards.get(position);
        holder.cardBg.setBackgroundResource(card.gradientDrawable);
        holder.cardName.setText(card.cardName);
        holder.networkLogo.setImageResource(card.networkLogoRes);
        holder.cardNumber.setText(card.cardNumber);
        holder.holderName.setText(card.holderName.toUpperCase());
        holder.expiry.setText(card.expiry);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cardBg;
        TextView cardName, cardNumber, holderName, expiry;
        ImageView networkLogo;

        CardViewHolder(View v) {
            super(v);
            cardBg      = v.findViewById(R.id.card_bg);
            cardName    = v.findViewById(R.id.cc_item_card_name);
            networkLogo = v.findViewById(R.id.cc_item_network_logo);
            cardNumber  = v.findViewById(R.id.cc_item_card_number);
            holderName  = v.findViewById(R.id.cc_item_holder_name);
            expiry      = v.findViewById(R.id.cc_item_expiry);
        }
    }
}
