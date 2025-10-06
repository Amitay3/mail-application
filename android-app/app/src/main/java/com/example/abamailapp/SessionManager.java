package com.example.abamailapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class SessionManager {
    private static int loggedInUserId = -1;
    private static String token = null;

    // ---- User ID ----
    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    // ---- JWT Token ----
    public static void setToken(String t) {
        token = t;
    }

    public static String getToken() {
        return token;
    }

    public static void clearSession() {
        loggedInUserId = -1;
        token = null;
    }

    public static void logout(Context context) {
        clearSession();

        // Go to login page
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // If called from an Activity, finish it
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
