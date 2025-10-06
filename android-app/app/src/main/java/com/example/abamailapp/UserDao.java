package com.example.abamailapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.abamailapp.entities.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE mail_address = :email")
    User getUserByEmail(String email);

    @Insert
    long insert(User user);

    @Insert
    long[] insert(User... users);

    @Update
    void update(User... users);

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserWithLabels> getUserWithLabels(int userId);
}

