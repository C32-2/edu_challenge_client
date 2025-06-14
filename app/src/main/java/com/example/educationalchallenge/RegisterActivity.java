package com.example.educationalchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView infoText;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initViews();
        initApi();
        setupListeners();
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        registerButton = findViewById(R.id.register_button);
        infoText = findViewById(R.id.info_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initApi() {
        apiService = ApiClient.getApiService();
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(username, email, password)) {
            return;
        }

        RegisterRequest request = new RegisterRequest(username, email, password);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        apiService.register(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                    navigateToStart();
                } else {
                    showErrorMessage("Пользователь с таким именем или email уже существует!");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                showErrorMessage("Ошибка сети! Попробуйте позже");
            }
        });
    }

    private boolean validateInput(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите данные до конца!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            showErrorMessage("Некорректная форма email!");
            return false;
        }

        if (password.length() < 6) {
            showErrorMessage("Пароль должен быть не короче 6 символов!");
            return false;
        }

        return true;
    }

    private void showErrorMessage(String message) {
        infoText.setText(message);
        infoText.setTextColor(ContextCompat.getColor(this, R.color.md_theme_light_error));
        infoText.setVisibility(TextView.VISIBLE);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToStart() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}