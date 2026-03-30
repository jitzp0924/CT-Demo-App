package com.jitendract.jitdemo.Reco;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jitendract.jitdemo.R;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    Context context;
    List<RecommendationCard> cardList;

    public RecommendationAdapter(Context context, List<RecommendationCard> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recommendation_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RecommendationCard card = cardList.get(position);

        holder.title.setText(card.getTitle());
        holder.button.setText(card.getButton());

        String iconUrl = card.getIcon();

        if (iconUrl != null && !iconUrl.equals("null") && !iconUrl.isEmpty()) {

            Glide.with(context)
                    .load(iconUrl)
                    .placeholder(R.drawable.personalinsurance)
                    .error(R.drawable.healthinsurance)
                    .into(holder.icon);

        } else {

            holder.icon.setImageResource(R.drawable.healthinsurance);
        }

        holder.itemView.setOnClickListener(v -> {

            String deeplink = card.getDeeplink();

            if (!deeplink.startsWith("http")) {
                deeplink = "https://" + deeplink;
            }

            Intent intent = new Intent();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        Button button;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.reco_card_text);
            button = itemView.findViewById(R.id.reco_card_button);
            icon = itemView.findViewById(R.id.reco_card_img);
        }
    }
}