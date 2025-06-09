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

        allTopics = new ArrayList<>();
        apiService = ApiClient.getApiService();
        jwtManager = new JwtManager(this);

        fetchTopics();
        initViews();
        setupListeners();

        answerList = new ArrayList<>();

        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerAdapter = new AnswerAdapter(answerList);
        answerRecyclerView.setAdapter(answerAdapter);
    }

    private void initViews() {
        topicSearch = findViewById(R.id.topic_search);
        answerRecyclerView = findViewById(R.id.answers_recycler_view);
        addQuestionButton = findViewById(R.id.addQuestionButton);
        questionEditText = findViewById(R.id.question_edit_text);
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

                    List<String> topicNames = new ArrayList<>();
                    for (TopicResponse topic : allTopics) {
                        topicNames.add(topic.toString());
                    }

                    ArrayAdapter<TopicResponse> adapter = new ArrayAdapter<>(
                            AddQuestionActivity.this,
                            R.layout.topic_item,
                            allTopics
                    );
                    topicSearch.setAdapter(adapter);
                    topicSearch.setThreshold(1);

                    topicSearch.setOnItemClickListener((parent, view, position, id) -> {
                        TopicResponse selectedTopic = (TopicResponse)parent.getItemAtPosition(position);
                        currentTopicId = selectedTopic.id;
                    });
                }
            }

            @Override
            public void onFailure(Call<List<TopicResponse>> call, Throwable t) {
                Log.e("API", "Ошибка загрузки тем", t);
            }
        });
    }

    private void addQuestion() {
        String text = questionEditText.getText().toString().trim();
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