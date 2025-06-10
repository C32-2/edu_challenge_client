package com.example.educationalchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.adapters.QuizAdapter;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.QuizResponse;
import com.example.educationalchallenge.dto.TopicResponse;
import com.example.educationalchallenge.security.JwtManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private Spinner topicSpinner;
    private RecyclerView quizRecyclerView;
    private QuizAdapter quizAdapter;
    private ApiService apiService;
    private JwtManager jwtManager;
    private List<TopicResponse> allTopics;
    private List<QuizResponse> quizzes = new ArrayList<>();
    private boolean isFirstSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        apiService = ApiClient.getApiService();
        jwtManager = new JwtManager(this);

        initViews();
        setupRecyclers();
        fetchTopics();
    }

    private void setupRecyclers() {
        quizAdapter = new QuizAdapter(quizzes, quiz -> {
            Intent intent = new Intent(this, QuizDetailActivity.class);
            intent.putExtra("quiz", quiz);
            startActivity(intent);
        });
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(quizAdapter);
    }

    private void initViews() {
        topicSpinner = findViewById(R.id.topicSpinner);
        quizRecyclerView = findViewById(R.id.quizRecyclerView);
    }

    private void fetchTopics() {
        apiService.getAllTopics("Bearer " + jwtManager.getToken())
                .enqueue(new Callback<List<TopicResponse>>() {
                    @Override
                    public void onResponse(Call<List<TopicResponse>> call, Response<List<TopicResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allTopics = response.body();
                            setupTopicSpinner(allTopics);
                        } else {
                            Log.e("API", "Ошибка загрузки тем: " + response.code() + ", " + response.message());
                            Toast.makeText(QuizActivity.this, "Не удалось загрузить темы", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TopicResponse>> call, Throwable t) {
                        Log.e("API", "Ошибка загрузки тем", t);
                        Toast.makeText(QuizActivity.this, "Ошибка сети при загрузке тем", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupTopicSpinner(List<TopicResponse> topics) {
        ArrayAdapter<TopicResponse> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                topics
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TopicResponse selected = (TopicResponse) parent.getItemAtPosition(position);
                loadQuizzes(selected.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ничего не делаем
            }
        });

        // Заставляем спиннер выбрать первый элемент и загрузить квизы по нему сразу
        if (!topics.isEmpty()) {
            topicSpinner.setSelection(0);
            loadQuizzes(topics.get(0).id);
        }
    }

    private void loadQuizzes(Long topicId) {
        apiService.getQuizzes(topicId, "Bearer " + jwtManager.getToken())
                .enqueue(new Callback<List<QuizResponse>>() {
                    @Override
                    public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<QuizResponse> loadedQuizzes = response.body();
                            quizAdapter.setQuizzes(loadedQuizzes);
                            if (loadedQuizzes.isEmpty()) {
                                Toast.makeText(
                                        QuizActivity.this,
                                        "Нет доступных квизов для этой темы",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    QuizActivity.this,
                                    "Ошибка загрузки квизов: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                        Toast.makeText(
                                QuizActivity.this,
                                "Ошибка загрузки квизов",
                                Toast.LENGTH_SHORT
                        ).show();
                        Log.e("API", "Ошибка загрузки квизов", t);
                    }
                });
    }
}
