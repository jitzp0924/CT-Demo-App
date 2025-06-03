package com.jitendract.jitdemo;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.google.android.material.card.MaterialCardView;
import com.jitendract.jitdemo.AppInboxModel.DataHandler;
import com.jitendract.jitdemo.AppInboxModel.InboxAdapter;

import java.util.ArrayList;

public class CustomInboxActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InboxAdapter inboxAdapter;
    private TextView inboxCountView;

    private DataHandler dataHandler;
    private CleverTapAPI cleverTapDefaultInstance;

    private MaterialCardView lastSelectedCard;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_inbox);

        recyclerView = findViewById(R.id.recyclerView);
        inboxCountView = findViewById(R.id.inboxCountView);

        // Initialize colors
        selectedColor = getResources().getColor(R.color.bank_dark_primary);

        // Initialize MaterialCardViews
        MaterialCardView allInboxButton = findViewById(R.id.all_tabs_Button);
        MaterialCardView transactionInboxButton = findViewById(R.id.transaction_tab_Button);
        MaterialCardView promotionInboxButton = findViewById(R.id.promotion_tab_Button);
        MaterialCardView requestInboxButton = findViewById(R.id.request_tab_Button);
        MaterialCardView regulatoryInboxButton = findViewById(R.id.regulatory_tab_Button);
        MaterialCardView offersInboxButton = findViewById(R.id.offers_tab_Button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        if (cleverTapDefaultInstance != null) {
            int inboxCount = cleverTapDefaultInstance.getInboxMessageCount();
            inboxCountView.setText(String.valueOf(inboxCount));

            ArrayList<CTInboxMessage> inboxMessages = cleverTapDefaultInstance.getAllInboxMessages();

            dataHandler = new DataHandler();
            dataHandler.categorizeMessages(inboxMessages);

            inboxMessages.get(0).getMessageId();

            // Initialize adapter with all inbox messages initially
            inboxAdapter = new InboxAdapter(dataHandler.getAllInboxMessages(), cleverTapDefaultInstance);
            recyclerView.setAdapter(inboxAdapter);

            // Set initial selected state
            lastSelectedCard = allInboxButton;
            setSelectedState(allInboxButton, R.id.all_tabs_text);

            // Click listeners for each tab
            transactionInboxButton.setOnClickListener(v -> handleTabClick(transactionInboxButton, dataHandler.getTransactionInboxMessages(), R.id.transaction_tab_text));
            allInboxButton.setOnClickListener(v -> handleTabClick(allInboxButton, dataHandler.getAllInboxMessages(), R.id.all_tabs_text));
            promotionInboxButton.setOnClickListener(v -> handleTabClick(promotionInboxButton, dataHandler.getPromotionInboxMessages(), R.id.promotion_tab_text));
            offersInboxButton.setOnClickListener(v -> handleTabClick(offersInboxButton, dataHandler.getOffersInboxMessages(), R.id.offers_tab_text));
            regulatoryInboxButton.setOnClickListener(v -> handleTabClick(regulatoryInboxButton, dataHandler.getRegulatoryInboxMessages(), R.id.regulatory_tab_text));
            requestInboxButton.setOnClickListener(v -> handleTabClick(requestInboxButton, dataHandler.getRequestInboxMessages(), R.id.request_tab_text));
        }
    }

    // Method to handle clicks on tabs and update RecyclerView adapter and UI state
    private void handleTabClick(MaterialCardView clickedCard, ArrayList<CTInboxMessage> messages, int textViewId) {
        if (lastSelectedCard != clickedCard) {
            inboxAdapter = new InboxAdapter(messages, cleverTapDefaultInstance);
            recyclerView.setAdapter(inboxAdapter);

            setSelectedState(clickedCard, textViewId);
            setUnselectedState(lastSelectedCard, getTextViewIdForCard(lastSelectedCard));
            lastSelectedCard = clickedCard;
        }
    }

    // Method to set selected state of a MaterialCardView
    private void setSelectedState(MaterialCardView cardView, int textViewId) {
        cardView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        TextView textView = findTextViewInsideCard(cardView, textViewId);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    // Method to set unselected state of a MaterialCardView
    private void setUnselectedState(MaterialCardView cardView, int textViewId) {
        cardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        TextView textView = findTextViewInsideCard(cardView, textViewId);
        if (textView != null) {
            textView.setTextColor(selectedColor);
        }
    }

    // Utility method to find a TextView inside a MaterialCardView by its ID
    private TextView findTextViewInsideCard(MaterialCardView cardView, int textViewId) {
        return cardView.findViewById(textViewId);
    }

    // Utility method to get the TextView ID for a given MaterialCardView
    private int getTextViewIdForCard(MaterialCardView cardView) {
        if (cardView.getId() == R.id.all_tabs_Button) {
            return R.id.all_tabs_text;
        } else if (cardView.getId() == R.id.transaction_tab_Button) {
            return R.id.transaction_tab_text;
        } else if (cardView.getId() == R.id.promotion_tab_Button) {
            return R.id.promotion_tab_text;
        } else if (cardView.getId() == R.id.request_tab_Button) {
            return R.id.request_tab_text;
        } else if (cardView.getId() == R.id.regulatory_tab_Button) {
            return R.id.regulatory_tab_text;
        } else if (cardView.getId() == R.id.offers_tab_Button) {
            return R.id.offers_tab_text;
        } else {
            return R.id.all_tabs_text; // Default to all_tabs_text if no specific match
        }
    }
}

