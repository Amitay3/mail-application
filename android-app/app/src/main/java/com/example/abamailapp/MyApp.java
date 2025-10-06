package com.example.abamailapp;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class MyApp extends Application {
    private static final String PREF_DARK_MODE = "pref_dark_mode";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Ensure pref exists for new users (default = light)
        if (!prefs.contains(PREF_DARK_MODE)) {
            prefs.edit().putBoolean(PREF_DARK_MODE, false).apply();
        }
        boolean dark = prefs.getBoolean(PREF_DARK_MODE, false);

        // Set global default mode so Activities created after this will use correct theme
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
