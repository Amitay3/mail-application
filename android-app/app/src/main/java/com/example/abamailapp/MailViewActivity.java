package com.example.abamailapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.repositories.LabelRepository;
import com.example.abamailapp.repositories.MailRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MailViewActivity extends AppCompatActivity {
    private int mailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_view);

        // Init DB
        AppDB db = DatabaseClient.getInstance(this);

        // Get mail ID and load from DB
        int mailId = getIntent().getIntExtra("mailId", -1);
        Mail mail = db.mailDao().get(mailId);

        String myEmail = db.userDao()
                .getUserById(SessionManager.getLoggedInUserId())
                .getMailAddress();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.mail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Find TextViews
        TextView subjectTv = findViewById(R.id.mail_subject);
        TextView senderTv = findViewById(R.id.mail_sender);
        TextView recipientTv = findViewById(R.id.mail_recipient);
        TextView bodyTv   = findViewById(R.id.mail_body);
        TextView timeTv = findViewById(R.id.mail_time);

        // Set data using helper
        subjectTv.setText(mail.getSubject());

        // Sender display (self/in/out logic)
        String senderDisplay = mail.getSenderEmail().equals(myEmail) &&
                mail.getRecipientEmail().equals(myEmail) ? "me" :
                mail.getSenderEmail();
        senderTv.setText("From: " + senderDisplay);

        // Recipient display
        String recipientDisplay = mail.getRecipientEmail().equals(myEmail) ? "me" :
                mail.getRecipientEmail();
        recipientTv.setText("To: " + recipientDisplay);

        // Time
        String time = mail.getTime();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            Date date = parser.parse(time);
            timeTv.setText(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            timeTv.setText(time);
        }

        // Body
        bodyTv.setText(mail.getContent());
    }

    // Inflate the three-dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mail_menu, menu);

        // Get mailId from intent
        int localMailId = getIntent().getIntExtra("mailId", -1);
        if (localMailId != -1) {
            AppDB db = DatabaseClient.getInstance(this);
            Executors.newSingleThreadExecutor().execute(() -> {
                Mail mail = db.mailDao().get(localMailId);
                final boolean isSpam = mail != null && mail.isSpam();
                runOnUiThread(() -> {
                    MenuItem spamItem = menu.findItem(R.id.action_spam);
                    if (spamItem != null) {
                        spamItem.setTitle(isSpam ? "Remove from spam" : "Add to spam");
                    }
                });
            });
        }

        return true;
    }

    // Handle menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_delete) {
            Log.d("MailViewActivity", "Delete menu clicked");
            int mailId = getIntent().getIntExtra("mailId", -1);
            if (mailId != -1) {
                // Get repository
                MailRepository repo = new MailRepository(this);

                // Get Mail from DB
                AppDB db = DatabaseClient.getInstance(this);
                Executors.newSingleThreadExecutor().execute(() -> {
                    Mail mail = db.mailDao().get(mailId);
                    Log.d("MailViewActivity", "Fetched mail = " + (mail != null ? mail.getSubject() : "null"));
                    if (mail != null) {
                        Log.d("MailViewActivity", "deleting mailId = " + mailId);
                        repo.deleteMail(mail);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Mail deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            }
            return true;
        } else if (id == R.id.action_label) {
            int mailId = getIntent().getIntExtra("mailId", -1);
            if (mailId == -1) {
                Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show();
                return true;
            }

            AppDB db = DatabaseClient.getInstance(this);
            LabelRepository repository = new LabelRepository(this);

            Executors.newSingleThreadExecutor().execute(() -> {
                Mail mail = db.mailDao().get(mailId); // synchronous Room call
                if (mail == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                List<Label> labels = db.labelDao().getAllLabels();
                // Fetch cross-references for this mail
                MailWithLabels mailWithLabels = db.mailLabelDao()
                        .getAllLabelsForMailSync(mail.getBackendId());

                runOnUiThread(() -> {
                    if (labels == null || labels.isEmpty()) {
                        Toast.makeText(this, "No labels available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] labelNames = new String[labels.size()];
                    boolean[] checkedItems = new boolean[labels.size()];

                    for (int i = 0; i < labels.size(); i++) {
                        Label label = labels.get(i);
                        labelNames[i] = label.getName();

                        // Check if this mail already has the label
                        checkedItems[i] = mailWithLabels != null &&
                                mailWithLabels.labels.stream()
                                        .anyMatch(l -> l.getBackendId().equals(label.getBackendId()));
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("Select Labels")
                            .setMultiChoiceItems(labelNames, checkedItems, (dialog, which, isChecked) -> {
                                checkedItems[which] = isChecked;
                            })
                            .setPositiveButton("Apply", (dialog, which) -> {
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    for (int i = 0; i < labels.size(); i++) {
                                        Label label = labels.get(i);
                                        boolean initiallyChecked = mailWithLabels.labels.stream()
                                                .anyMatch(l -> l.getBackendId().equals(label.getBackendId()));
                                        boolean nowChecked = checkedItems[i];

                                        if (!initiallyChecked && nowChecked) {
                                            repository.addLabelToMail(mail, label);
                                        } else if (initiallyChecked && !nowChecked) {
                                            repository.deleteLabelFromMail(mail, label);
                                        }
                                    }
                                });

                                Toast.makeText(this, "Labels updated", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
            });
            return true;
        } else if (id == R.id.action_spam) {
            int mailId = getIntent().getIntExtra("mailId", -1);
            if (mailId != -1) {
                // Get repository and DB
                MailRepository repo = new MailRepository(this);
                AppDB db = DatabaseClient.getInstance(this);
                // Fetch mail in background
                Executors.newSingleThreadExecutor().execute(() -> {
                    Mail mail = db.mailDao().get(mailId);
                    if (mail != null) {
                        if (mail.isSpam()) {
                            Log.d("MailViewActivity", "removing mailId = " + mailId + " from spam");
                            repo.removeMailFromSpam(mail);
                            runOnUiThread(() -> {
                                Toast.makeText(MailViewActivity.this, "Removed from Spam", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        } else {
                            Log.d("MailViewActivity", "marking mailId = " + mailId + " as spam");
                            repo.addMailToSpam(mail);
                            runOnUiThread(() -> {
                                Toast.makeText(MailViewActivity.this, "Added to Spam", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
