package com.example.abamailapp.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.AppDB;
import com.example.abamailapp.DatabaseClient;
import com.example.abamailapp.api.MailAPI;
import com.example.abamailapp.entities.Mail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailRepository {
    private final MailAPI mailAPI;
    private final AppDB db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MailRepository(Context context) {
        mailAPI = new MailAPI();
        db = DatabaseClient.getInstance(context);
    }

    // --- Fetch mail lists ---
    public LiveData<List<Mail>> getInbox(String userEmail) {
        refreshInboxFromBackend(userEmail);
        return db.mailDao().getInbox(userEmail);
    }

    public LiveData<List<Mail>> getSent(String userEmail) {
        refreshSentFromBackend(userEmail);
        return db.mailDao().getSent(userEmail);
    }

    public LiveData<List<Mail>> getDrafts(String userEmail) {
        refreshDraftsFromBackend(userEmail);
        return db.mailDao().getDrafts(userEmail);
    }

    public LiveData<List<Mail>> getSpam(String userEmail) {
        refreshSpamFromBackend(userEmail);
        return db.mailDao().getSpam(userEmail);
    }

    public LiveData<List<Mail>> searchMails(String query) {
        MutableLiveData<List<Mail>> result = new MutableLiveData<>();

        // Fetch from backend
        refreshSearchFromBackend(query, new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                result.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                result.postValue(Collections.emptyList());
            }
        });

        return result; // LiveData containing only the search results
    }


    // --- Send mail ---
    public void sendMail(Mail mail, Callback<Mail> userCallback) {
        // Insert locally first
        executor.execute(() -> {
            if (!mail.isDraft() || mail.getId() == 0) {
                // Only insert if sent mail or new draft (no local id)
                db.mailDao().insert(mail);
            }
        });

        // Send directly to backend
        mailAPI.sendMail(mail, new Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Mail backendMail = response.body(); // this now has backendId

                    executor.execute(() -> {
                        // Delete the old draft (local version) by backendId or local id
                        if (mail.isDraft()) {
                            if (mail.getBackendId() != null) {
                                db.mailDao().deleteByBackendId(mail.getBackendId());
                            } else if (mail.getId() != 0) {
                                db.mailDao().delete(mail); // fallback for first-time draft
                            }
                        }

                        // Insert backend-confirmed mail locally (sent mail)
                        db.mailDao().insert(backendMail);
                    });
                }

                if (userCallback != null) userCallback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                if (userCallback != null) userCallback.onFailure(call, t);
            }
        });
    }

    // --- Delete mail ---
    public void deleteMail(Mail mail) {
        // Delete locally
        executor.execute(() -> db.mailDao().delete(mail));
        // delete from backend using backendId
        if (mail.getBackendId() != null) {
            Log.d("MailRepository", "Deleting mail from backend with ID: " + mail.getBackendId());
            mailAPI.deleteMail(mail.getBackendId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("MailRepository", "Backend delete response: " + response.code());
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("MailRepository", "Backend delete failed", t);
                }
            });
        }
    }
    // --- Update mail ---
    public void updateMail(Mail mail, Callback<Mail> userCallback) {
        if (mail.getBackendId() == null || mail.getBackendId().isEmpty()) {
            Log.e("MailRepository", "updateDraft called without backendId");
            return;
        }

        if (mail.isDraft()) {
            // Update on backend
            mailAPI.updateMail(mail.getBackendId(), mail, new Callback<Mail>() {
                @Override
                public void onResponse(Call<Mail> call, Response<Mail> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Mail backendMail = response.body(); // updated draft from backend

                        executor.execute(() -> {
                            // Delete old draft first
                            if (mail.getBackendId() != null) {
                                db.mailDao().deleteByBackendId(mail.getBackendId());
                            } else if (mail.getId() != 0) {
                                db.mailDao().delete(mail); // fallback if no backendId
                            }

                            // Insert backend-confirmed updated draft
                            db.mailDao().insert(backendMail);
                        });
                    }

                    if (userCallback != null) userCallback.onResponse(call, response);
                }

                @Override
                public void onFailure(Call<Mail> call, Throwable t) {
                    if (userCallback != null) userCallback.onFailure(call, t);
                }
            });
        } else {
            Log.w("MailRepository", "updateDraft called on non-draft mail, ignoring");
        }
    }


    // --- Backend sync ---
    public void refreshInboxFromBackend(String userEmail) {
        mailAPI.fetchInbox(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        db.mailDao().deleteInboxForUser(userEmail);
                        db.mailDao().insert(response.body());
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {}
        });
    }


    public void refreshSentFromBackend(String userEmail) {
        mailAPI.fetchSent(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        db.mailDao().deleteSentForUser(userEmail);
                        db.mailDao().insert(response.body());
                    });
                }
            }
            @Override public void onFailure(Call<List<Mail>> call, Throwable t) {}
        });
    }

    public void refreshDraftsFromBackend(String userEmail) {
        // Clean up nulls
        executor.execute(() -> db.mailDao().deleteMailsWithNullBackendId());

        mailAPI.fetchDrafts(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        db.mailDao().deleteDraftsForUser(userEmail);
                        db.mailDao().insert(response.body());
                    });
                }
            }
            @Override public void onFailure(Call<List<Mail>> call, Throwable t) {}
        });
    }

    public void refreshSpamFromBackend(String userEmail) {
        mailAPI.fetchSpam(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mail> mails = response.body();

                    // DEBUG all fetched spam mails
                    for (Mail m : mails) {
                        Log.d("MailRepository-----", "SPAM from backend -> backendId=" + m.getBackendId()
                                + " recipient=" + m.getRecipientEmail()
                                + " isSpam(before)=" + m.isSpam()
                                + " time=" + m.getTime());
                    }

                    // Mark as spam and ensure recipient is the expected user (defensive)
                    for (Mail m : mails) {
                        m.setSpam(true);
                        // optionally force recipient to userEmail if backend doesn't include it
                        if (m.getRecipientEmail() == null || m.getRecipientEmail().isEmpty()) {
                            m.setRecipientEmail(userEmail);
                        }
                    }

                    // Insert into DB on background thread
                    executor.execute(() -> {
                        try {
                            db.mailDao().deleteSpamForUser(userEmail);
                            db.mailDao().insert(mails);
                        } catch (Exception e) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Log.e("MailRepository-----", "fetchSpam failed", t);
            }
        });
    }

    private void refreshSearchFromBackend(String query, Callback<List<Mail>> callback) {
        mailAPI.searchMails(query, new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Return the mails directly to UI
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void addMailToSpam(Mail mail) {
        if (mail == null) return;

        // Mark locally
        executor.execute(() -> {
            mail.setSpam(true);
            db.mailDao().insert(mail);
        });

        // Mark on backend
        if (mail.getBackendId() != null) {
            mailAPI.addMailToSpam(mail.getBackendId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("MailRepository", "Backend addToSpam response: " + response.code());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("MailRepository", "Backend addToSpam failed", t);
                }
            });
        }
    }

    public void removeMailFromSpam(Mail mail) {
        if (mail == null) return;

        // Unmark locally
        executor.execute(() -> {
            mail.setSpam(false);
            db.mailDao().insert(mail);
        });

        // Unmark on backend
        if (mail.getBackendId() != null) {
            mailAPI.removeMailFromSpam(mail.getBackendId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("MailRepository", "Backend removeMailFromSpam response: " + response.code());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("MailRepository", "Backend removeMailFromSpam failed", t);
                }
            });
        }
    }
}
