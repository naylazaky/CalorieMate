package com.example.caloriemate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriemate.database.DatabaseHelper;
import com.example.caloriemate.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPassword, etCalorieTarget;
    private Button btnRegister;
    private TextView tvGoLogin;

    private DatabaseHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCalorieTarget = findViewById(R.id.etCalorieTarget);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String targetStr = etCalorieTarget.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Full name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password minimum 6 characters");
            return;
        }
        if (TextUtils.isEmpty(targetStr)) {
            etCalorieTarget.setError("Calorie target is required");
            return;
        }

        int calorieTarget = Integer.parseInt(targetStr);
        User user = new User(name, email, password, calorieTarget);

        btnRegister.setEnabled(false);

        executor.execute(() -> {
            boolean success = dbHelper.registerUser(user);
            handler.post(() -> {
                btnRegister.setEnabled(true);
                if (success) {
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}