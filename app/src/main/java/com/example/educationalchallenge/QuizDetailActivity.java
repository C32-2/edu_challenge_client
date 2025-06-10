package com.example.educationalchallenge;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.adapters.AnswerDetailAdapter;
import com.example.educationalchallenge.dto.Answer;
import com.example.educationalchallenge.dto.QuestionResponse;
import com.example.educationalchallenge.dto.QuizResponse;

import java.util.ArrayList;
import java.util.List;

public class QuizDetailActivity extends AppCompatActivity {

    private TextView questionCountTextView;
    private TextView quizTitleTextView;
    private TextView questionSolvedTextView;
    private TextView questionTextView;
    private QuizResponse quiz;
    private List<QuestionResponse> questions;
    private List<Answer> currentAnswers;
    private RecyclerView answerRecyclerView;
    private AnswerDetailAdapter answerDetailAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentQuestionIndex = 0;
    private int solvedQuestionsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_detail);

        quiz = (QuizResponse) getIntent().getSerializableExtra("quiz");
        if (quiz == null || quiz.questions == null || quiz.questions.isEmpty()) {
            finish();
            return;
        }

        initViews();
        setupQuiz();
        showCurrentQuestion();
    }

    private void initViews() {
        questionCountTextView = findViewById(R.id.questionCount);
        quizTitleTextView = findViewById(R.id.quizTitle);
        questionSolvedTextView = findViewById(R.id.questionSolved);
        questionTextView = findViewById(R.id.currentQuestionText);
        answerRecyclerView = findViewById(R.id.answerDetailRecyclerView);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupQuiz() {
        quizTitleTextView.setText(quiz.title);
        questions = quiz.questions;
        currentQuestionIndex = 0;
        solvedQuestionsCount = 0;
        updateSolvedQuestionsText();
    }

    private void showCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            QuestionResponse currentQuestion = questions.get(currentQuestionIndex);

            questionCountTextView.setText(String.format("Вопрос %d/%d",
                    currentQuestionIndex + 1,
                    questions.size()
            ));

            questionTextView.setText(currentQuestion.text);

            currentAnswers = currentQuestion.answers;

            if (answerDetailAdapter == null) {
                answerDetailAdapter = new AnswerDetailAdapter(currentAnswers, this::onAnswerSelected);
                answerRecyclerView.setAdapter(answerDetailAdapter);
            } else {
                answerDetailAdapter.setAnswers(currentAnswers);
            }
        }
    }

    private void onAnswerSelected(Answer selectedAnswer) {
        if (selectedAnswer.isCorrect) {
            solvedQuestionsCount++;
            updateSolvedQuestionsText();
            nextQuestion();
        } else {
            updateSolvedQuestionsText();
            nextQuestion();
        }
    }

    private void updateSolvedQuestionsText() {
        questionSolvedTextView.setText(String.format(
                "Решено: %d/%d (%.0f%%)",
                solvedQuestionsCount,
                questions.size(),
                ((double) solvedQuestionsCount / questions.size()) * 100
        ));
    }


    private void nextQuestion() {
        if (currentQuestionIndex + 1 < questions.size()) {
            currentQuestionIndex++;
            showCurrentQuestion();
        } else {
            Toast.makeText(this, "Викторина завершена!", Toast.LENGTH_LONG).show();
            setupResult();
        }
    }

    private void setupResult() {
        questionSolvedTextView.setVisibility(VISIBLE);
        questionTextView.setText("Викторина завершена!");
        answerDetailAdapter.setAnswers(new ArrayList<>());
    }
}