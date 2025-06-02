package com.example.educationalchallenge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.AddTopicRequest;
import com.example.educationalchallenge.security.JwtManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTopicActivity extends AppCompatActivity {

    private Button addTopicButton;
    private EditText topicEditText;
    private ApiService apiService;
    private JwtManager jwtManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_topic);

        jwtManager = new JwtManager(this);

        initViews();
        initApi();
        setupListeners();
    }

    private void initApi() {
        apiService = ApiClient.getApiService();
    }

    private void setupListeners() {
        addTopicButton.setOnClickListener(v -> performAdd());
    }

    private void initViews() {
        addTopicButton = findViewById(R.id.add_topic_button);
        topicEditText = findViewById(R.id.topic_edit_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void performAdd() {
        String name = topicEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Заполните поле!", Toast.LENGTH_SHORT).show();
            return;
        }

        AddTopicRequest request = new AddTopicRequest(name);
        progressBar.setVisibility(View.VISIBLE);
        apiService.addTopic(request, "Bearer " + jwtManager.getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddTopicActivity.this, "Топик успешно добавлен!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    topicEditText.setText("");
                } else {
                    Log.e("API", "Ошибка при добавлении: " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}