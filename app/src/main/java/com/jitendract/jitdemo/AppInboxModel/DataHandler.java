package com.jitendract.jitdemo.AppInboxModel;

import com.clevertap.android.sdk.inbox.CTInboxMessage;

import java.util.ArrayList;

public class DataHandler {

    private ArrayList<CTInboxMessage> allInboxMessages = new ArrayList<>();
    private ArrayList<CTInboxMessage> transactionInboxMessage = new ArrayList<>();
    private ArrayList<CTInboxMessage> promotionInboxMessage = new ArrayList<>();
    private ArrayList<CTInboxMessage> requestInboxMessage = new ArrayList<>();
    private ArrayList<CTInboxMessage> regulatoryInboxMessage = new ArrayList<>();
    private ArrayList<CTInboxMessage> offersInboxMessage = new ArrayList<>();

    public void categorizeMessages(ArrayList<CTInboxMessage> inboxMessages) {
        for (CTInboxMessage message : inboxMessages) {
            String tag = message.getTags().toString().toLowerCase();

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
    }

    public ArrayList<CTInboxMessage> getAllInboxMessages() {
        return allInboxMessages;
    }

    public ArrayList<CTInboxMessage> getTransactionInboxMessages() {
        return transactionInboxMessage;
    }

    public ArrayList<CTInboxMessage> getPromotionInboxMessages() {
        return promotionInboxMessage;
    }

    public ArrayList<CTInboxMessage> getRequestInboxMessages() {
        return requestInboxMessage;
    }

    public ArrayList<CTInboxMessage> getRegulatoryInboxMessages() {
        return regulatoryInboxMessage;
    }

    public ArrayList<CTInboxMessage> getOffersInboxMessages() {
        return offersInboxMessage;
    }
}
