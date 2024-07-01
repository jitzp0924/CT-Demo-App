package com.jitendract.jitdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomInboxActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InboxAdapter inboxAdapter;
    private TextView inboxCountView;

    ArrayList<CTInboxMessage> allInboxMessages,transactionInboxMessage,promotionInboxMessage,requestInboxMessage,regulatoryInboxMessage,offersInboxMessage;
    Button allInboxButton, transactionInboxButton,promotionInboxButton,requestInboxButton,regulatoryInboxButton,offersInboxButton;

    static CleverTapAPI cleverTapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_inbox);

        recyclerView = findViewById(R.id.recyclerView);
        inboxCountView = findViewById(R.id.inboxCountView);

        //
        allInboxButton = findViewById(R.id.all_tabs_Button);
        transactionInboxButton = findViewById(R.id.transaction_tab_Button);
        promotionInboxButton = findViewById(R.id.promotion_tab_Button);
        requestInboxButton = findViewById(R.id.request_tab_Button);
        regulatoryInboxButton = findViewById(R.id.regulatory_tab_Button);
        offersInboxButton = findViewById(R.id.offers_tab_Button);

        //
        allInboxMessages = new ArrayList<>();
        transactionInboxMessage = new ArrayList<>();
        promotionInboxMessage = new ArrayList<>();
        requestInboxMessage = new ArrayList<>();
        regulatoryInboxMessage = new ArrayList<>();
        offersInboxMessage = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        if (cleverTapDefaultInstance != null) {
            // Get the inbox count and set it to the TextView
            int inboxCount = cleverTapDefaultInstance.getInboxMessageCount();
            inboxCountView.setText(String.valueOf(inboxCount));

            // Fetch all inbox messages
            ArrayList<CTInboxMessage> inboxMessages = cleverTapDefaultInstance.getAllInboxMessages();

            for (CTInboxMessage message : inboxMessages) {
                String tag = message.getTags().toString().toLowerCase();

                Log.v("Inbox Tag",""+tag);

                if ("[offers]".equals(tag)) {
                    offersInboxMessage.add(message);
                    allInboxMessages.add(message);
                } else if ("[transaction]".equals(tag)) {
                    transactionInboxMessage.add(message);
                    allInboxMessages.add(message);
                } else if ("[promotion]".equals(tag)) {
                    promotionInboxMessage.add(message);
                    allInboxMessages.add(message);
                } else if ("[request]".equals(tag)) {
                    requestInboxMessage.add(message);
                    allInboxMessages.add(message);
                } else if ("[regulatory]".equals(tag)) {
                    regulatoryInboxMessage.add(message);
                    allInboxMessages.add(message);
                } else {
                    allInboxMessages.add(message);
                }
            }

            inboxAdapter = new InboxAdapter(inboxMessages);
            recyclerView.setAdapter(inboxAdapter);

            transactionInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(transactionInboxMessage);
                recyclerView.setAdapter(inboxAdapter);
            });
            allInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(allInboxMessages);
                recyclerView.setAdapter(inboxAdapter);
            });
            promotionInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(promotionInboxMessage);
                recyclerView.setAdapter(inboxAdapter);
            });
            offersInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(offersInboxMessage);
                recyclerView.setAdapter(inboxAdapter);
            });
            regulatoryInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(regulatoryInboxMessage);
                recyclerView.setAdapter(inboxAdapter);
            });
            requestInboxButton.setOnClickListener(v ->{
                inboxAdapter = new InboxAdapter(requestInboxMessage);
                recyclerView.setAdapter(inboxAdapter);
            });
        }
    }

    private static class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {



        private final ArrayList<CTInboxMessage> inboxMessages;

        public InboxAdapter(ArrayList<CTInboxMessage> inboxMessages) {
            this.inboxMessages = inboxMessages;
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

            // Get message type, tags, and read status
            List<String> tags = message.getTags();
            String messageType = String.valueOf(message.getType());
            boolean isRead = message.isRead();
            String messageId = message.getMessageId();

            // Set title and content
            String bgColor = message.getBgColor();
            String messageColor = message.getInboxMessageContents().get(0).getMessageColor();
            String titleColor = message.getInboxMessageContents().get(0).getTitleColor();
            String messageText = message.getInboxMessageContents().get(0).getMessage();
            String titleText = message.getInboxMessageContents().get(0).getTitle();

            // Set colors and background based on read status
            if (isRead) {
                holder.itemView.setBackgroundResource(R.drawable.card_background_read);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.card_background_unread);
            }
            holder.messageTitle.setTextColor(Color.parseColor(titleColor));
            holder.messageContent.setTextColor(Color.parseColor(messageColor));

            // Set title and content text
            holder.messageTitle.setText(titleText);
            holder.messageContent.setText(messageText);

            // Check for image URL
            String imageURL = message.getInboxMessageContents().get(0).getMedia();
            if (imageURL != null && !imageURL.isEmpty()) {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(imageURL).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }

            // Check message type for icon display
            if ("message-icon".equals(messageType)) {
                String iconUrl = message.getInboxMessageContents().get(0).getIcon();
                if (iconUrl != null && !iconUrl.isEmpty()) {
                    holder.iconImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(iconUrl).into(holder.iconImageView);
                } else {
                    holder.iconImageView.setVisibility(View.GONE);
                }
            } else {
                holder.iconImageView.setVisibility(View.GONE);
            }
            if (!isRead) {
                cleverTapDefaultInstance.markReadInboxMessage(messageId);
                cleverTapDefaultInstance.pushInboxNotificationViewedEvent(messageId);
            }
        }




        @Override
        public int getItemCount() {
            return inboxMessages.size();
        }

        static class InboxViewHolder extends RecyclerView.ViewHolder {

            TextView messageTitle;
            TextView messageContent;
            ImageView imageView,iconImageView;

            public InboxViewHolder(@NonNull View itemView) {
                super(itemView);
                messageTitle = itemView.findViewById(R.id.messageTitle);
                messageContent = itemView.findViewById(R.id.messageContent);
                imageView = itemView.findViewById(R.id.imageView);
                iconImageView = itemView.findViewById(R.id.iconImageView);// Assuming you have an ImageView in notification_card_item.xml
            }
        }
    }
}