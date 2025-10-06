package com.example.abamailapp;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abamailapp.adapters.LabelAdapter;
import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.UserRepository;
import com.example.abamailapp.viewmodels.LabelViewModel;

public class ManageLabelsActivity extends AppCompatActivity {
    private LabelViewModel labelViewModel;
    private LabelAdapter labelAdapter;
    private RecyclerView recyclerView;
    private AppDB db;
    private UserRepository userRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_labels);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        recyclerView = findViewById(R.id.recyclerViewLabels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        labelAdapter = new LabelAdapter(new LabelAdapter.OnLabelActionListener() {
            @Override
            public void onEdit(Label label) {
                // Show dialog with EditText
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageLabelsActivity.this);
                builder.setTitle("Edit Label");

                final EditText input = new EditText(ManageLabelsActivity.this);
                input.setText(label.getName());
                input.setSelection(label.getName().length()); // place cursor at end
                builder.setView(input);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty() && !newName.equals(label.getName())) {
                        label.setName(newName);
                        labelViewModel.updateLabel(label);
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            }
            @Override
            public void onDelete(Label label) {
                labelViewModel.deleteLabel(label);
            }
        });
        recyclerView.setAdapter(labelAdapter);

        db = DatabaseClient.getInstance(this);
        int userId = SessionManager.getLoggedInUserId();
        userRepository = new UserRepository(this);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        User user = db.userDao().getUserById(userId);

        if (user == null) {
            // User not in Room, fetch from backend
            userRepository.fetchUserById(userId, fetchedUser -> {
                if (fetchedUser != null) {
                    new Thread(() -> {
                        db.userDao().insert(fetchedUser); // save to Room
                        runOnUiThread(() -> {
                            labelViewModel.getLabels()
                                    .observe(this, labels -> labelAdapter.setLabels(labels));
                        });
                    }).start();
                }
            });
        } else {
            // User exists in Room
            labelViewModel.getLabels()
                    .observe(this, labels -> labelAdapter.setLabels(labels));
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}