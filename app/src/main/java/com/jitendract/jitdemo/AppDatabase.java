package com.jitendract.jitdemo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Wallet.class, Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();
    public abstract TransactionDao transactionDao();
}

