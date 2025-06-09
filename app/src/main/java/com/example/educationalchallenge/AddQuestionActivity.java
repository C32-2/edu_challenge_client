package com.example.educationalchallenge;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.adapters.AnswerAdapter;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.AddQuestionRequest;
import com.example.educationalchallenge.dto.TopicResponse;
import com.example.educationalchallenge.items.AnswerItem;
import com.example.educationalchallenge.security.JwtManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionActivity extends AppCompatActivity {

    private List<TopicResponse> allTopics;
    private AutoCompleteTextView topicSearch;
    private ApiService apiService;
    private JwtManager jwtManager;
    private Long currentTopicId;
    private EditText questionEditText;
    private RecyclerView answerRecyclerView;
    private List<AnswerItem> answerList;
    private Button addQuestionButton;
    private AnswerAdapter answerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_question);

        apiService = ApiClient.getApiService();
        jwtManager = new JwtManager(this);
        allTopics = new ArrayList<>();
        answerList = new ArrayList<>();

        initViews();
        initRecyclerView();
        setupListeners();
        fetchTopics();
    }

    private void initViews() {
        topicSearch = findViewById(R.id.topic_search);
        answerRecyclerView = findViewById(R.id.answers_recycler_view);
        addQuestionButton = findViewById(R.id.addQuestionButton);
        questionEditText = findViewById(R.id.question_edit_text);
    }

    private void initRecyclerView() {
        answerAdapter = new AnswerAdapter(answerList);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerRecyclerView.setAdapter(answerAdapter);
    }

    private void setupListeners() {
        addQuestionButton.setOnClickListener(v -> addQuestion());
    }

    private void fetchTopics() {
        apiService.getAllTopics("Bearer " + jwtManager.getToken())
                .enqueue(new Callback<List<TopicResponse>>() {
                    @Override
                    public void onResponse(Call<List<TopicResponse>> call, Response<List<TopicResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allTopics = response.body();
                            setupTopicSearch(allTopics);
                        } else {
                            Log.e("API", "Ошибка загрузки тем: " + response.code() + ", " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TopicResponse>> call, Throwable t) {
                        Log.e("API", "Ошибка загрузки тем", t);
                    }
                });
    }

    private void setupTopicSearch(List<TopicResponse> topics) {
        ArrayAdapter<TopicResponse> adapter = new ArrayAdapter<>(
                this,
                R.layout.topic_item,
                topics
        );
        topicSearch.setAdapter(adapter);
        topicSearch.setThreshold(0);

        topicSearch.setOnClickListener(v -> {
            if (topicSearch.getText().toString().isEmpty()) {
                topicSearch.showDropDown();
            }
        });

        topicSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !topicSearch.isPopupShowing()) {
                topicSearch.showDropDown();
            }
        });
    }

    private void addQuestion() {
        String text = questionEditText.getText().toString().trim();

        if (text.isEmpty() || currentTopicId == null || answerList.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        AddQuestionRequest request = new AddQuestionRequest(text, currentTopicId, answerList);
        apiService.addQuestion(request, "Bearer " + jwtManager.getToken())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddQuestionActivity.this, "Вопрос добавлен!", Toast.LENGTH_SHORT).show();
                            clearActivity();
                        } else {
                            Log.e("API", "Ошибка при добавлении: " + response.code() + ", " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API", "Ошибка сети при добавлении вопроса", t);
                    }
                });
    }

    private void clearActivity() {
        answerList.clear();
        topicSearch.setText("");
        questionEditText.setText("");
        answerAdapter.notifyDataSetChanged();
    }
}