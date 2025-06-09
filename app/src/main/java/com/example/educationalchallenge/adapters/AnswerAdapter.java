package com.example.educationalchallenge.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.items.AnswerItem;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ANSWER = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

    private final List<AnswerItem> answers;

    public AnswerAdapter(List<AnswerItem> answers) {
        this.answers = answers;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == answers.size()) ? VIEW_TYPE_BUTTON : VIEW_TYPE_ANSWER;
    }

    @Override
    public int getItemCount() {
        return answers.size() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ANSWER) {
            View view = inflater.inflate(R.layout.answer_item, parent, false);
            return new AnswerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.add_button_item, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AnswerViewHolder) {
            AnswerItem answer = answers.get(position);
            AnswerViewHolder vh = (AnswerViewHolder) holder;

            // Удаляем слушатели, если есть
            if (vh.textWatcher != null) {
                vh.editText.removeTextChangedListener(vh.textWatcher);
            }

            vh.editText.setText(answer.getText());
            vh.checkBox.setChecked(answer.isCorrect());

            // Новый TextWatcher
            vh.textWatcher = new SimpleTextWatcher(s -> {
                answer.setText(s.toString());
            });
            vh.editText.addTextChangedListener(vh.textWatcher);

            vh.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                answer.setCorrect(isChecked);
            });

            vh.removeButton.setOnClickListener(v -> {
                if (answers.size() > 1) {
                    answers.remove(position);
                    notifyItemRemoved(position);
                }
            });

        } else if (holder instanceof AddButtonViewHolder) {
            ((AddButtonViewHolder) holder).addButton.setOnClickListener(v -> {
                answers.add(new AnswerItem());
                notifyItemInserted(answers.size() - 1);
            });
        }
    }

    public boolean isValid() {
        for (AnswerItem item : answers) {
            if (item.getText().trim().isEmpty()) return false;
        }
        return true;
    }

    public boolean hasCorrectAnswer() {
        for (AnswerItem item : answers) {
            if (item.isCorrect()) return true;
        }
        return false;
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        CheckBox checkBox;
        ImageButton removeButton;
        SimpleTextWatcher textWatcher;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edit_text_answer);
            checkBox = itemView.findViewById(R.id.checkbox_correct);
            removeButton = itemView.findViewById(R.id.button_remove_answer);
        }
    }

    static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        Button addButton;

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.button_add_answer);
        }
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final OnTextChangedCallback callback;

        public SimpleTextWatcher(OnTextChangedCallback callback) {
            this.callback = callback;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {
            callback.onTextChanged(s);
        }

        interface OnTextChangedCallback {
            void onTextChanged(CharSequence s);
        }
    }
}
