package com.example.abamailapp.api;

import android.util.Log;

import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailRequest;
import com.example.abamailapp.repositories.AuthInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailAPI {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private final BackendAPI backendAPI;


    public MailAPI() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        backendAPI = retrofit.create(BackendAPI.class);
    }

    public void fetchInbox(Callback<List<Mail>> callback) { backendAPI.getInbox().enqueue(callback); }
    public void fetchSent(Callback<List<Mail>> callback) { backendAPI.getSent().enqueue(callback); }
    public void fetchDrafts(Callback<List<Mail>> callback) { backendAPI.getDrafts().enqueue(callback); }
    public void fetchSpam(Callback<List<Mail>> callback) { backendAPI.getSpam().enqueue(callback); }

    public void searchMails(String query, Callback<List<Mail>> callback) {
        backendAPI.searchMails(query).enqueue(callback);
    }
    public void sendMail(Mail mail, Callback<Mail> callback) {
        MailRequest request = new MailRequest(mail.getSenderEmail(),
                mail.getRecipientEmail(),
                mail.getSubject(),
                mail.getContent(),
                mail.isDraft());

        backendAPI.createMail(request).enqueue(callback);
    }
    // Delete mail by backend ID
    public void deleteMail(String mailBackendId, Callback<Void> callback) {
        backendAPI.deleteMail(mailBackendId).enqueue(callback);
    }

    // Update mail by backend ID
    public void updateMail(String backendId, Mail mail, Callback<Mail> callback) {
        MailRequest request = new MailRequest(
                mail.getSenderEmail(),
                mail.getRecipientEmail(),
                mail.getSubject(),
                mail.getContent(),
                mail.isDraft()
        );

        backendAPI.updateMail(backendId, request).enqueue(callback);
        // If sending, delete draft
        if (!mail.isDraft()) {
            backendAPI.deleteMail(backendId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    // Successfully deleted draft after sending
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    Log.d("MailAPI", "Failed to delete draft after sending: " + t.getMessage());
                }
            });
        }
    }

    public void addMailToSpam(String mailBackendId, Callback<Void> callback) {
        backendAPI.addMailToSpam(mailBackendId).enqueue(callback);
    }

    public void removeMailFromSpam(String mailBackendId, Callback<Void> callback) {
        backendAPI.removeMailFromSpam(mailBackendId).enqueue(callback);
    }
}
