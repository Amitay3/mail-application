package com.example.abamailapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.repositories.LabelRepository;
import com.example.abamailapp.repositories.MailRepository;

import java.util.List;

import retrofit2.Callback;

public class MailViewModel extends AndroidViewModel {
    private final MailRepository mailRepository;
    private LabelRepository labelRepository;

    public MailViewModel(@NonNull Application application) {
        super(application);
        mailRepository = new MailRepository(application);
        labelRepository = new LabelRepository(application);
    }

    // LiveData for folders (Room provides automatic updates)
    public LiveData<List<Mail>> getInbox(String userEmail) {
        return mailRepository.getInbox(userEmail);
    }

    public LiveData<List<Mail>> getSent(String userEmail) {
        return mailRepository.getSent(userEmail);
    }

    public LiveData<List<Mail>> getDrafts(String userEmail) {
        return mailRepository.getDrafts(userEmail);
    }

    public LiveData<List<Mail>> getSpam(String userEmail) {
        return mailRepository.getSpam(userEmail);
    }

    public LiveData<List<Mail>> fetchMailsForLabel(String labelBackendId) { return labelRepository.getMailsForLabel(labelBackendId); }
    public void addMailToSpam(Mail mail) {
        mailRepository.addMailToSpam(mail);
    }

    public void removeMailFromSpam(Mail mail) {
        mailRepository.removeMailFromSpam(mail);
    }

    public LiveData<List<Mail>> searchMails( String query) {
        return mailRepository.searchMails(query);
    }

    // Send mail (updates Room and backend)
    public void sendMail(Mail mail, Callback<Mail> callback) {
        mailRepository.sendMail(mail, callback);
    }

    // Delete mail (updates Room and backend)
    public void deleteMail(Mail mail) {
        mailRepository.deleteMail(mail);
    }

    // Optional: force refresh from backend
    public void refreshInbox(String userEmail) {
        mailRepository.refreshInboxFromBackend(userEmail);
    }

    public void refreshSent(String userEmail) {
        mailRepository.refreshSentFromBackend(userEmail);
    }

    public void refreshDrafts(String userEmail) {
        mailRepository.refreshDraftsFromBackend(userEmail);
    }

    public void refreshSpam(String userEmail) {
        mailRepository.refreshSpamFromBackend(userEmail);
    }


}
