package com.example.educationalchallenge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.AuthRequest;
import com.example.educationalchallenge.dto.AuthResponse;
import com.example.educationalchallenge.security.JwtManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private JwtManager jwtManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        jwtManager = new JwtManager(this);
        apiService = ApiClient.getApiService();

        jwtManager.clearToken();

        if (jwtManager.getToken() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerTextView = findViewById(R.id.register_text);

        loginButton.setOnClickListener(v -> performLogin());

        setupClickableRegisterText(registerTextView);
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRequest request = new AuthRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jwtManager.saveToken(response.body().getToken());
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(StartActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(StartActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickableRegisterText(TextView registerTextView) {
        String fullText = registerTextView.getText().toString();
        String clickablePart = "Зарегистрироваться";
        int startIndex = fullText.indexOf(clickablePart);

        if (startIndex >= 0) {
            SpannableString spannable = new SpannableString(fullText);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                }

                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(ds.linkColor);
                }
            };

            spannable.setSpan(clickableSpan, startIndex, startIndex + clickablePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            registerTextView.setText(spannable);
            registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
            registerTextView.setHighlightColor(Color.TRANSPARENT);
        }
    }
}
