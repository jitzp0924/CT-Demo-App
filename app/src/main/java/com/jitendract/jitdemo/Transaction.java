package com.jitendract.jitdemo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category; // Fastag, Recharge, DTH, etc.
    public double amount;
    public String transactionType; // "debit" or "credit"
    public long timestamp; // Epoch time of the transaction
    public String status; // "success" or "failure"
    public String userData; // Data specific to the transaction category (phone number, ID, etc.)

    // Constructor
    public Transaction(String category, double amount, String transactionType, long timestamp, String status, String userData) {
        this.category = category;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        this.status = status;
        this.userData = userData;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }
}
