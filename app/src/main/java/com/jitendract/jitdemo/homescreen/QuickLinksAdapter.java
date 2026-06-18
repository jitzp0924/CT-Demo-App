package com.jitendract.jitdemo.homescreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jitendract.jitdemo.R;
import java.util.List;

public class QuickLinksAdapter extends RecyclerView.Adapter<QuickLinksAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(QuickLinkItem item);
    }

    private final List<QuickLinkItem> items;
    private final OnItemClickListener listener;

    public QuickLinksAdapter(List<QuickLinkItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_link, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickLinkItem item = items.get(position);
        holder.icon.setImageResource(item.iconRes);
        holder.label.setText(item.label);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView label;

        ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.ql_icon);
            label = v.findViewById(R.id.ql_label);
        }
    }
}
