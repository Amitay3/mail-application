package com.example.abamailapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.abamailapp.viewmodels.LabelViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AddLabelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_label);

        LabelViewModel labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);


        int userId = SessionManager.getLoggedInUserId();
        TextInputEditText labelInput = findViewById(R.id.label_input);
        Button save = findViewById(R.id.btn_save_label);
        Button cancel = findViewById(R.id.btn_cancel);

        save.setOnClickListener(v -> {
            String labelName = labelInput.getText() != null ? labelInput.getText().toString().trim() : "";
            if (labelName.isEmpty()) {
                labelInput.setError("Label name cannot be empty");
                return;
            }
            labelViewModel.addLabel(labelName);

            Intent result = new Intent();
            result.putExtra("new_label_name", labelName);
            setResult(RESULT_OK, result);
            finish();
        });

        cancel.setOnClickListener(v -> finish());
    }
}