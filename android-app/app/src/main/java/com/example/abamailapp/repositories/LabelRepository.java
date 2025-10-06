package com.example.abamailapp.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.abamailapp.AppDB;
import com.example.abamailapp.DatabaseClient;
import com.example.abamailapp.SessionManager;
import com.example.abamailapp.api.LabelAPI;
import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.MailLabelCrossRef;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelRepository {

    private final AppDB db;
    private final LabelAPI labelAPI;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LabelRepository(Context context) {
        db = DatabaseClient.getInstance(context);
        labelAPI = new LabelAPI();
    }

    public LiveData<List<Label>> getLabels() {
        // Return LiveData from Room
        return db.labelDao().getAllLabelsLive();
    }

    public LiveData<List<Mail>> getMailsForLabel(String labelBackendId) {
        int userId = SessionManager.getLoggedInUserId();
        return db.mailLabelDao().getMailsForLabelSync(labelBackendId, userId);
    }

    public void fetchLabelsFromBackend() {
        Log.d("LabelRepository", "Fetching labels from backend...");
        labelAPI.fetchAllLabels(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Label> labels = response.body();

                    // Log for debugging
                    for (Label label : labels) {
                        Log.d("LabelRepository", "Label from backend: id=" + label.getId() + ", name=" + label.getName());
                    }

                    // Insert/update in Room
                    executor.execute(() -> {
                        db.labelDao().deleteAll();
                        db.labelDao().insert(labels);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.e("LabelRepository", "Failed to fetch labels from backend", t);
            }
        });
    }

    public void addLabel(String labelName) {
        labelAPI.createLabel(labelName, new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                fetchLabelsFromBackend(); // Refresh after creation
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                Log.e("LabelRepository", "Failed to create label", t);
            }
        });
    }

    public void updateLabel(Label label) {
        executor.execute(() -> db.labelDao().update(label));

        labelAPI.updateLabel(label.getBackendId(), label.getName(), new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                fetchLabelsFromBackend();
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                Log.e("LabelRepository", "Failed to update label", t);
            }
        });
    }

    public void deleteLabel(Label label) {
        executor.execute(() -> db.labelDao().delete(label));

        labelAPI.deleteLabel(label.getBackendId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchLabelsFromBackend(); // Refresh after deletion
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LabelRepository", "Failed to delete label", t);
            }
        });
    }


    public void addLabelToMail(Mail mail, Label label) {
        labelAPI.addLabelToMail(mail.getBackendId(), label.getBackendId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                executor.execute(() -> {
                    MailLabelCrossRef crossRef = new MailLabelCrossRef();
                    crossRef.mailBackendId = mail.getBackendId();
                    crossRef.labelBackendId = label.getBackendId();
                    crossRef.userId = SessionManager.getLoggedInUserId();
                    db.mailLabelDao().addLabelToMail(crossRef);
                });
                fetchLabelsFromBackend();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LabelRepository", "Failed to label mail", t);
            }
        });
    }

    public void getLabelsForMail(Mail mail) {
        labelAPI.getLabelsForMail(mail.getBackendId(), new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Label> labels = response.body();

                    executor.execute(() -> {
                        // Insert labels into labels table
                        db.labelDao().insert(labels);

                        // Create cross-references for this mail
                        for (Label label : labels) {
                            MailLabelCrossRef crossRef = new MailLabelCrossRef();
                            crossRef.mailBackendId = mail.getBackendId();
                            crossRef.labelBackendId = label.getBackendId();
                            crossRef.userId = SessionManager.getLoggedInUserId();
                            db.mailLabelDao().addLabelToMail(crossRef);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.e("LabelRepository", "Failed to fetch labels for mail", t);
            }
        });
    }

    public void deleteLabelFromMail(Mail mail, Label label) {
        int userId = SessionManager.getLoggedInUserId();
        db.mailLabelDao().removeLabelFromMail(mail.getBackendId(), label.getBackendId(), userId);
        labelAPI.removeLabelFromMail(mail.getBackendId(), label.getBackendId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchLabelsFromBackend();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LabelRepository", "Failed to remove label from mail", t);
            }
        });
    }

    public void hasMailGotLabel(Mail mail, Label label, Callback<Boolean> callback) {
        labelAPI.getLabelsForMail(mail.getBackendId(), new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                boolean hasLabel = false;
                if (response.isSuccessful() && response.body() != null) {
                    for (Label l : response.body()) {
                        if (l.equals(label)) {
                            hasLabel = true;
                            break;
                        }
                    }
                }
                callback.onResponse(null, Response.success(hasLabel)); // use Response<Boolean>
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {

            }
        });
    }
}

