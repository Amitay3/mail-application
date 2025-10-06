package com.example.abamailapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.abamailapp.entities.Mail;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.MailRepository;
import com.example.abamailapp.viewmodels.ComposeViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ComposeActivity extends AppCompatActivity {

    private MailRepository mailRepository;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        ComposeViewModel viewModel = new ComposeViewModel(getApplication());

        // Observe toast messages
        viewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) {
                runOnUiThread(() ->
                        Toast.makeText(ComposeActivity.this, msg, Toast.LENGTH_LONG).show()
                );
            }
        });

        Toolbar toolbar = findViewById(R.id.compose_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get views
        EditText to_edit = findViewById(R.id.to_edit);
        EditText subject_edit = findViewById(R.id.subject_edit);
        EditText content_edit = findViewById(R.id.content_edit);
        ExtendedFloatingActionButton send_btn = findViewById(R.id.send_btn);

        // Extract intent extras once
        Intent intent = getIntent();
        boolean editingDraft = intent.getBooleanExtra("isDraft", false);
        String backendId = intent.getStringExtra("backendId");

        // Pre-fill fields if editing a draft
        if (editingDraft) {
            to_edit.setText(intent.getStringExtra("to"));
            subject_edit.setText(intent.getStringExtra("subject"));
            content_edit.setText(intent.getStringExtra("body"));
        }

        // Send button logic
        send_btn.setOnClickListener(v -> {
            String receiverEmail = to_edit.getText().toString().trim();
            String subject = subject_edit.getText().toString();
            String content = content_edit.getText().toString();

            AppDB db = DatabaseClient.getInstance(this);
            User sender = db.userDao().getUserById(SessionManager.getLoggedInUserId());
            String senderEmail = sender.getMailAddress();
            String isoNow = java.time.Instant.now().toString();

            if (editingDraft && backendId != null) {
                // Sending a draft
                viewModel.sendMail(senderEmail, receiverEmail, subject, content,
                        isoNow, false,
                        new ComposeViewModel.MailSendCallback() {
                            @Override
                            public void onSuccess() {
                                // Delete the draft locally and on backend
                                MailRepository repo = new MailRepository(getApplicationContext());
                                Mail draft = db.mailDao().getByBackendId(backendId);
                                if (draft != null) {
                                    repo.deleteMail(draft);
                                }
                                // Move back to main activity
                                Toast.makeText(ComposeActivity.this, "Mail sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(String errorMessage) {
                                // removed double toast
                            }
                        });
            } else {
                // Normal send
                viewModel.sendMail(senderEmail, receiverEmail, subject, content,
                        isoNow, false,
                        new ComposeViewModel.MailSendCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ComposeActivity.this, "Mail sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(String errorMessage) {}
                        });
            }
        });
    }


    // Handle back button in toolbar
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EditText to_edit = findViewById(R.id.to_edit);
            EditText subject_edit = findViewById(R.id.subject_edit);
            EditText content_edit = findViewById(R.id.content_edit);

            String receiverEmail = to_edit.getText().toString().trim();
            String subject = subject_edit.getText().toString();
            String content = content_edit.getText().toString();

            // Do nothing if all fields empty
            if (receiverEmail.isEmpty() && subject.isEmpty() && content.isEmpty()) {
                finish();
                return true;
            }

            AppDB db = DatabaseClient.getInstance(this);
            User sender = db.userDao().getUserById(SessionManager.getLoggedInUserId());
            String senderEmail = sender.getMailAddress();

            ComposeViewModel viewModel = new ComposeViewModel(getApplication());
            Intent intent = getIntent();

            boolean editingDraft = intent.getBooleanExtra("isDraft", false);
            String backendId = intent.getStringExtra("backendId");
            int localId = intent.getIntExtra("id", -1);
            String isoNow = java.time.Instant.now().toString();

            // Create Mail object using both IDs
            Mail draft = new Mail(senderEmail, receiverEmail, subject, content,
                    isoNow, true, false, backendId);

            // Keep local ID if editing
            if (editingDraft && localId != -1) {
                draft.setId(localId);
            }
            // If editing an existing draft, update it; else, save new draft
            if (editingDraft && backendId != null) {
                // Update existing draft
                viewModel.updateMail(draft, new ComposeViewModel.MailSendCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ComposeActivity.this, "Draft updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ComposeActivity.this, "Error updating draft", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                // Save new draft
                viewModel.sendMail(senderEmail, receiverEmail, subject, content,
                        isoNow, true,
                        new ComposeViewModel.MailSendCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ComposeActivity.this, "Saved as draft", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(ComposeActivity.this, "Error saving draft", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

