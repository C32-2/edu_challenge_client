package com.example.educationalchallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.dto.Answer;

import java.util.List;

public class AnswerDetailAdapter extends RecyclerView.Adapter<AnswerDetailAdapter.ViewHolder> {

    private List<Answer> answers;
    private final OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(Answer item);
    }

    public AnswerDetailAdapter(List<Answer> answers, OnOptionClickListener listener) {
        this.answers = answers;
        this.listener = listener;
    }

    public void setAnswers(List<Answer> newAnswers) {
        this.answers = newAnswers;
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
        Answer item = answers.get(position);
        holder.text.setText(item.text);
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(item));
    }

    @Override
    public int getItemCount() {
        return answers == null ? 0 : answers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.question_text);
        }
    }
}

