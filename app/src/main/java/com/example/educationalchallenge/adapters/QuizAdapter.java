package com.example.educationalchallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.dto.QuizResponse;

import java.util.List;



public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizResponse> quizzes;

    public QuizAdapter(List<QuizResponse> quizzes) {
        this.quizzes = quizzes;
    }

    public void setQuizzes(List<QuizResponse> newQuizzes) {
        this.quizzes = newQuizzes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizResponse quiz = quizzes.get(position);
        holder.quizTitle.setText(quiz.title);
        // Можно прикрутить иконку по id или title
    }

    @Override
    public int getItemCount() {
        return quizzes != null ? quizzes.size() : 0;
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
        }
    }
}