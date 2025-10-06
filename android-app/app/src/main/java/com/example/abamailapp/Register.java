package com.example.abamailapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.entities.User;
import com.example.abamailapp.repositories.UserRepository;

import java.io.IOException;

public class Register extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private ImageView profileImage;
    private Button registerButton;
    private Button backToLoginButton;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        profileImage = findViewById(R.id.profileImage);
        registerButton = findViewById(R.id.registerButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmedPassword = confirmPasswordEditText.getText().toString().trim();

            if (!email.endsWith("@abamail.com") || email.length() < 13) {
                Toast.makeText(this, "Email must be in format of example@abamail.com", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                Toast.makeText(this, "Password must have 8+ characters including 1 uppercase letter and 1 digit", Toast.LENGTH_LONG).show();
                return;
            }
            if (!password.equals(confirmedPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            MutableLiveData<String> error = new MutableLiveData<>();
            MutableLiveData<User> result = new MutableLiveData<>();

            error.observe(this, msg -> {
                // Show error message
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                Log.e("RegisterActivity", "Error: " + msg);
            });
            result.observe(this, user -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Login.class);
                intent.putExtra("email", emailEditText.getText().toString().trim());
                startActivity(intent);
                finish();
            });

            UserRepository userRepository = new UserRepository(getApplicationContext());
            try {
                userRepository.register(name, password, confirmedPassword, email, selectedImageUri, error, result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Navigate back to login
        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profileImage.setImageURI(selectedImageUri);
                    }
                }
        );

        // Open gallery when clicked
        profileImage.setOnClickListener(v -> openGallery());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}