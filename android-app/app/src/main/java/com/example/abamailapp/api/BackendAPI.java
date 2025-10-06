package com.example.abamailapp.api;

import com.example.abamailapp.entities.CreateLabelRequest;
import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.LoginResponse;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailRequest;
import com.example.abamailapp.entities.RegisterResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface BackendAPI {

    @POST("tokens")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Multipart
    @POST("users")
    Call<RegisterResponse> registerUser(
            @Part("userName") RequestBody name,
            @Part("password") RequestBody password,
            @Part("verifiedPassword") RequestBody verifiedPassword,
            @Part("mailAddress") RequestBody email,
            @Part MultipartBody.Part image  // optional
    );

    // --- MAILS ---
    @GET("mails")
    Call<List<Mail>> getLast50Mails();

    @GET("mails/inbox")
    Call<List<Mail>> getInbox();
    @GET("mails/sent")
    Call<List<Mail>> getSent();
    @GET("mails/drafts")
    Call<List<Mail>> getDrafts();
    @GET("mails/spam")
    Call<List<Mail>> getSpam();


    @POST("mails")
    Call<Mail> createMail(@Body MailRequest mailRequest);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String id);

    @PATCH("mails/{id}")
    Call<Mail> updateMail(@Path("id") String id, @Body MailRequest mailRequest);

    // --- USERS ---
    @POST("users")
    Call<com.example.abamailapp.entities.User> createUser(@Body com.example.abamailapp.entities.User user);

    @GET("users/{id}")
    Call<com.example.abamailapp.entities.User> getUserById(@Path("id") int id);

    @GET("labels")
    Call<List<Label>> getAllLabels();

    @POST("labels")
    Call<Label> createLabel(@Body CreateLabelRequest label);

    @GET("labels/{id}")
    Call<Label> getLabelById(@Path("id") int id);

    @PATCH("labels/{id}")
    Call<Label> updateLabel(@Path("id") String id, @Body CreateLabelRequest label);

    @DELETE("labels/{id}")
    Call<Void> deleteLabel(@Path("id") String id);

    // Label - Mail actions
    @POST("labels/mail")
    Call<Void> addLabelToMail(@Body MailLabelRequest request);

    @GET("labels/mail/{mailId}")
    Call<List<Label>> getLabelsForMail(@Path("mailId") String mailId);

    @GET("labels/folder/{labelId}")
    Call<List<Mail>> getMailsForLabel(@Path("labelId") String labelId);

    @DELETE("labels/mail/{mailId}/{labelId}")
    Call<Void> removeLabelFromMail(@Path("mailId") String mailId, @Path("labelId") String labelId);

    @GET("mails/search/{query}")
    Call<List<Mail>> searchMails(@Path("query") String query);

    @POST("mails/spam/{id}")
    Call<Void> addMailToSpam(@Path("id") String id);

    @DELETE("mails/spam/{id}")
    Call<Void> removeMailFromSpam(@Path("id") String id);
}
