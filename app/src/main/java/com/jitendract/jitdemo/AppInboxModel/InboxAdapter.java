package com.jitendract.jitdemo.AppInboxModel;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.jitendract.jitdemo.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {

    private ArrayList<CTInboxMessage> inboxMessages;
    static CleverTapAPI cleverTapDefaultInstance;


    public InboxAdapter(ArrayList<CTInboxMessage> inboxMessages,CleverTapAPI cleverTapDefaultInstance) {
        this.inboxMessages = inboxMessages;
        this.cleverTapDefaultInstance = cleverTapDefaultInstance;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card_item, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        CTInboxMessage message = inboxMessages.get(position);
        InboxDataHelper dataHelper = new InboxDataHelper(message);
        holder.bind(dataHelper);
    }

    @Override
    public int getItemCount() {
        return inboxMessages.size();
    }

    public static class InboxViewHolder extends RecyclerView.ViewHolder {

        TextView messageTitle;
        TextView messageContent;
        ImageView imageView, iconImageView;

        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTitle = itemView.findViewById(R.id.messageTitle);
            messageContent = itemView.findViewById(R.id.messageContent);
            imageView = itemView.findViewById(R.id.imageView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        public void bind(InboxDataHelper dataHelper) {
            boolean isRead = dataHelper.isMessageRead();
            String bgColor = dataHelper.getBackgroundColor();
            String messageColor = dataHelper.getMessageColor();
            String titleColor = dataHelper.getTitleColor();
            String messageText = dataHelper.getMessageText();
            String titleText = dataHelper.getTitleText();
            String imageURL = dataHelper.getImageUrl();
            String iconUrl = dataHelper.getIconUrl();

            if (isRead) {
                itemView.setBackgroundResource(R.drawable.card_background_read);
            } else {
                itemView.setBackgroundResource(R.drawable.card_background_unread);
            }
            messageTitle.setTextColor(Color.parseColor(titleColor));
            messageContent.setTextColor(Color.parseColor(messageColor));
            messageTitle.setText(titleText);
            messageContent.setText(messageText);

            if (imageURL != null && !imageURL.isEmpty()) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(imageURL).into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }

            if ("message-icon".equals(dataHelper.getMessageType()) && iconUrl != null && !iconUrl.isEmpty()) {
                iconImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(iconUrl).into(iconImageView);
            } else {
                iconImageView.setVisibility(View.GONE);
            }

            if (!isRead) {
                cleverTapDefaultInstance.markReadInboxMessage(dataHelper.getMessageId());
                cleverTapDefaultInstance.pushInboxNotificationViewedEvent(dataHelper.getMessageId());
            }
        }
    }
}
