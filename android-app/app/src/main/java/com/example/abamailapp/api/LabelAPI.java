package com.example.abamailapp.api;

import com.example.abamailapp.entities.CreateLabelRequest;
import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.repositories.AuthInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LabelAPI {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private final BackendAPI backendAPI;

    public LabelAPI() {
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

    // --- GET all labels ---
    public void fetchAllLabels(Callback<List<Label>> callback) {
        backendAPI.getAllLabels().enqueue(callback);
    }

    // --- POST create label ---
    public void createLabel(String name, Callback<Label> callback) {
        backendAPI.createLabel(new CreateLabelRequest(name)).enqueue(callback);
    }

    // --- GET label by id ---
    public void getLabelById(int id, Callback<Label> callback) {
        backendAPI.getLabelById(id).enqueue(callback);
    }

    // --- PATCH update label ---
    public void updateLabel(String id, String name, Callback<Label> callback) {
        backendAPI.updateLabel(id, new CreateLabelRequest(name)).enqueue(callback);
    }

    // --- DELETE label ---
    public void deleteLabel(String id, Callback<Void> callback) {
        backendAPI.deleteLabel(id).enqueue(callback);
    }

    // --- POST add label to mail ---
    public void addLabelToMail(String mailId, String labelId, Callback<Void> callback) {
        backendAPI.addLabelToMail(new MailLabelRequest(mailId, labelId)).enqueue(callback);
    }

    // --- GET all labels for a mail ---
    public void getLabelsForMail(String mailId, Callback<List<Label>> callback) {
        backendAPI.getLabelsForMail(mailId).enqueue(callback);
    }

    // --- GET all mails for a label ---
    public void getMailsForLabel(String labelId, Callback<List<Mail>> callback) {
        backendAPI.getMailsForLabel(labelId).enqueue(callback);
    }

    // --- DELETE remove label from mail ---
    public void removeLabelFromMail(String mailId, String labelId, Callback<Void> callback) {
        backendAPI.removeLabelFromMail(mailId, labelId).enqueue(callback);
    }
}