package com.example.educationalchallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.dto.QuestionResponse;

import java.util.ArrayList;
import java.util.List;

public class ReceivedQuestionAdapter extends RecyclerView.Adapter<ReceivedQuestionAdapter.ViewHolder> {

    private List<QuestionResponse> questions;  // теперь не final
    private final OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(QuestionResponse item);
    }

    public ReceivedQuestionAdapter(List<QuestionResponse> questions, OnOptionClickListener listener) {
        this.questions = questions != null ? questions : new ArrayList<>();
        this.listener = listener;
    }

    public void setQuestions(List<QuestionResponse> newQuestions) {
        this.questions = newQuestions != null ? newQuestions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.received_question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionResponse item = questions.get(position);
        holder.text.setText(item.text);
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(item));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.question_text);
        }
    }
}

