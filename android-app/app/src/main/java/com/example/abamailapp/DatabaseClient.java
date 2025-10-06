package com.example.abamailapp;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static AppDB instance;

    @SuppressWarnings("deprecation")
    public static AppDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDB.class, "app-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
