package com.jitendract.jitdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.jitendract.jitdemo.AppInboxModel.DataHandler;
import com.jitendract.jitdemo.AppInboxModel.InboxAdapter;

import java.util.ArrayList;

public class CustomInboxActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InboxAdapter inboxAdapter;
    private TextView inboxCountView;

    private DataHandler dataHandler;
    private CleverTapAPI cleverTapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_inbox);

        recyclerView = findViewById(R.id.recyclerView);
        inboxCountView = findViewById(R.id.inboxCountView);

        Button allInboxButton = findViewById(R.id.all_tabs_Button);
        Button transactionInboxButton = findViewById(R.id.transaction_tab_Button);
        Button promotionInboxButton = findViewById(R.id.promotion_tab_Button);
        Button requestInboxButton = findViewById(R.id.request_tab_Button);
        Button regulatoryInboxButton = findViewById(R.id.regulatory_tab_Button);
        Button offersInboxButton = findViewById(R.id.offers_tab_Button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        if (cleverTapDefaultInstance != null) {
            int inboxCount = cleverTapDefaultInstance.getInboxMessageCount();
            inboxCountView.setText(String.valueOf(inboxCount));

            ArrayList<CTInboxMessage> inboxMessages = cleverTapDefaultInstance.getAllInboxMessages();

            dataHandler = new DataHandler();
            dataHandler.categorizeMessages(inboxMessages);

            inboxAdapter = new InboxAdapter(dataHandler.getAllInboxMessages(),cleverTapDefaultInstance);
            recyclerView.setAdapter(inboxAdapter);

            transactionInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getTransactionInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });

            allInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getAllInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });

            promotionInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getPromotionInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });

            offersInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getOffersInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });

            regulatoryInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getRegulatoryInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });

            requestInboxButton.setOnClickListener(v -> {
                inboxAdapter = new InboxAdapter(dataHandler.getRequestInboxMessages(),cleverTapDefaultInstance);
                recyclerView.setAdapter(inboxAdapter);
            });
        }
    }
}
