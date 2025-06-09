package com.example.educationalchallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.dto.QuestionResponse;

import java.util.ArrayList;
import java.util.List;

public class SelectedQuestionAdapter extends RecyclerView.Adapter<SelectedQuestionAdapter.ViewHolder> {

    private List<QuestionResponse> questions;
    private final OnDeleteClickListener deleteListener;

    // Интерфейс для колбэка удаления
    public interface OnDeleteClickListener {
        void onDeleteClick(QuestionResponse item);
    }

    public SelectedQuestionAdapter(List<QuestionResponse> questions, OnDeleteClickListener deleteListener) {
        this.questions = questions != null ? questions : new ArrayList<>();
        this.deleteListener = deleteListener;
    }

    public void setQuestions(List<QuestionResponse> newQuestions) {
        this.questions = newQuestions != null ? newQuestions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionResponse item = questions.get(position);
        holder.text.setText(item.text);

        // Клик только по кнопке удаления
        holder.removeButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageButton removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.question_text);
            removeButton = itemView.findViewById(R.id.button_remove_answer);
        }
    }
}
