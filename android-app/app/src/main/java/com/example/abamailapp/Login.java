package com.example.abamailapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.UserRepository;

public class Login extends AppCompatActivity {
    private UserRepository userRepository;
    private AppDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Delete the Room database named "app-db"
        // getApplicationContext().deleteDatabase("app-db");
        super.onCreate(savedInstanceState);

        // Check if already logged in
        int userId = SessionManager.getLoggedInUserId();
        if (userId != -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(getApplicationContext());

        EditText emailEditText = findViewById(R.id.emailEditText);
        String prefillEmail = getIntent().getStringExtra("email");
        if (prefillEmail != null) {
            emailEditText.setText(prefillEmail);
        }
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            MutableLiveData<String> error = new MutableLiveData<>();
            MutableLiveData<User> result = new MutableLiveData<>();

            error.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
            result.observe(this, user -> {
                SessionManager.setLoggedInUserId(user.getId());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });

            userRepository.login(email, password, error, result);
        });

        TextView createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(v -> {
            Intent goToCreate = new Intent(this, Register.class);
            startActivity(goToCreate);
        });
    }
}
