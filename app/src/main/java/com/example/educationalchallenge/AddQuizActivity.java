package com.example.educationalchallenge;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.educationalchallenge.adapters.ReceivedQuestionAdapter;
import com.example.educationalchallenge.adapters.SelectedQuestionAdapter;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.AddQuizRequest;
import com.example.educationalchallenge.dto.QuestionResponse;
import com.example.educationalchallenge.dto.TopicResponse;
import com.example.educationalchallenge.security.JwtManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuizActivity extends AppCompatActivity {

    private Button addQuizButton;
    private EditText questionText;
    private AutoCompleteTextView topicSearch;
    private RecyclerView receivedRecyclerView, selectedRecyclerView;

    private ApiService apiService;
    private JwtManager jwtManager;

    private List<TopicResponse> allTopics = new ArrayList<>();
    private List<QuestionResponse> receivedQuestions = new ArrayList<>();
    private List<QuestionResponse> selectedQuestions = new ArrayList<>();

    private ReceivedQuestionAdapter receivedAdapter;
    private SelectedQuestionAdapter selectedAdapter;

    private Long currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_quiz);

        apiService = ApiClient.getApiService();
        jwtManager = new JwtManager(this);

        initViews();
        setupRecyclerViews();
        setupListeners();
        fetchTopics();
    }

    private void initViews() {
        addQuizButton = findViewById(R.id.addQuizButton);
        questionText = findViewById(R.id.question_input);
        topicSearch = findViewById(R.id.topic_input);
        receivedRecyclerView = findViewById(R.id.receivedRecyclerView);
        selectedRecyclerView = findViewById(R.id.selectedRecyclerView);
    }

    private void setupRecyclerViews() {
        receivedAdapter = new ReceivedQuestionAdapter(receivedQuestions, question -> {
            if (!selectedQuestions.contains(question)) {
                selectedQuestions.add(question);

                selectedAdapter.notifyDataSetChanged();
            }
        });

        selectedAdapter = new SelectedQuestionAdapter(selectedQuestions, question -> {
            selectedAdapter.removeQuestion(question);
        });

        receivedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        receivedRecyclerView.setAdapter(receivedAdapter);
        selectedRecyclerView.setAdapter(selectedAdapter);
    }

    private void setupListeners() {
        addQuizButton.setOnClickListener(v -> showTitleDialog());

        questionText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                fetchQuestions();
            }
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchQuestions();
            }
        });
    }

    private void fetchTopics() {
        apiService.getAllTopics(authHeader()).enqueue(new Callback<List<TopicResponse>>() {
            @Override
            public void onResponse(Call<List<TopicResponse>> call, Response<List<TopicResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTopics = response.body();
                    setupTopicDropdown();
                } else {
                    logError("Не удалось получить темы", null);
                }
            }

            @Override
            public void onFailure(Call<List<TopicResponse>> call, Throwable t) {
                logError("Ошибка загрузки тем", t);
            }
        });
    }

    private void setupTopicDropdown() {
        ArrayAdapter<TopicResponse> adapter = new ArrayAdapter<>(
                this, R.layout.topic_item, allTopics
        );

        topicSearch.setAdapter(adapter);
        topicSearch.setThreshold(0);
        topicSearch.setOnItemClickListener((parent, view, position, id) -> {
            TopicResponse selected = (TopicResponse) parent.getItemAtPosition(position);
            currentTopicId = selected.id;
            fetchQuestions();
        });
    }

    private void fetchQuestions() {
        String query = questionText.getText().toString().trim();
        if (currentTopicId == null) return;

        apiService.searchQuestion(currentTopicId, query, authHeader())
                .enqueue(new Callback<List<QuestionResponse>>() {
                    @Override
                    public void onResponse(Call<List<QuestionResponse>> call, Response<List<QuestionResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            receivedQuestions = response.body();
                            receivedAdapter.setQuestions(receivedQuestions);
                        } else {
                            logError("Не удалось загрузить вопросы", null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QuestionResponse>> call, Throwable t) {
                        logError("Ошибка загрузки вопросов", t);
                    }
                });
    }

    private void addQuiz(String title) {
        if (currentTopicId == null || selectedQuestions.isEmpty()) {
            Toast.makeText(this, "Выберите тему и хотя бы один вопрос", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> questionIds = selectedQuestions.stream()
                .map(q -> q.id)
                .collect(Collectors.toList());

        AddQuizRequest request = new AddQuizRequest(title, currentTopicId, questionIds);

        apiService.addQuiz(request, authHeader()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddQuizActivity.this, "Квиз успешно создан!", Toast.LENGTH_SHORT).show();
                } else {
                    logError("Не удалось создать квиз", null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logError("Ошибка при создании квиза", t);
            }
        });
    }

    private void showTitleDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quiz_title);

        EditText titleInput = dialog.findViewById(R.id.editTextTitle);
        Button buttonCreate = dialog.findViewById(R.id.buttonCreate);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonCreate.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название квиза", Toast.LENGTH_SHORT).show();
                return;
            }

            addQuiz(title);
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private String authHeader() {
        return "Bearer " + jwtManager.getToken();
    }

    private void logError(String message, Throwable t) {
        Log.e("AddQuizActivity", message, t);
    }
}