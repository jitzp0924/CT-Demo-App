package com.jitendract.jitdemo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WalletDao {
    @Insert
    void insert(Wallet wallet);

    @Update
    void update(Wallet wallet);

    @Query("SELECT * FROM wallet WHERE id = 1")
    Wallet getWallet();
}
