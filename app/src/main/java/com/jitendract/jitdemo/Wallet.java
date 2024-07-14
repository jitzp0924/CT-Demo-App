package com.jitendract.jitdemo;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallet")
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private double amount;

    public Wallet() {}

    public Wallet(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}


