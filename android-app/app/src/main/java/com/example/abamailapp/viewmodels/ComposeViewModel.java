package com.example.abamailapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.repositories.MailRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ComposeViewModel extends AndroidViewModel {

    public interface MailSendCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
    private final MailRepository mailRepository;

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public ComposeViewModel(@NonNull Application application) {
        super(application);
        mailRepository = new MailRepository(application);
    }

    public void sendMail(String senderEmail, String recipientEmail, String subject,
                         String content, String time, boolean isDraft,
                         MailSendCallback callback) {

        Mail mail = new Mail(senderEmail, recipientEmail, subject, content, time, isDraft, false, null);

        mailRepository.sendMail(mail, new retrofit2.Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful()) {
                    if (isDraft) {
                        toastMessage.postValue("Saved as draft");
                    }
                    if (callback != null) callback.onSuccess();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        toastMessage.postValue(response.code() + "\n" + errorBody);
                        if (callback != null) callback.onError(response.code() + "\n" + errorBody);
                    } catch (IOException e) {
                        toastMessage.postValue("Error parsing response");
                        if (callback != null) callback.onError("Error parsing response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
                if (callback != null) callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    // Update existing draft
    public void updateMail(Mail mail, MailSendCallback callback) {
        mailRepository.updateMail(mail, new retrofit2.Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful()) {
                    toastMessage.postValue("Draft updated");
                    if (callback != null) callback.onSuccess();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        toastMessage.postValue(response.code() + "\n" + errorBody);
                        if (callback != null) callback.onError(errorBody);
                    } catch (IOException e) {
                        toastMessage.postValue("Error parsing response");
                        if (callback != null) callback.onError("Error parsing response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
                if (callback != null) callback.onError("Network error: " + t.getMessage());
            }
        });
    }

}
