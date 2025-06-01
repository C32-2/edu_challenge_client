package com.example.educationalchallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.models.OptionItem;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {

    private final List<OptionItem> options;
    private final OnOptionClickListener listener;
    public interface OnOptionClickListener {
        void onOptionClick(OptionItem item);
    }

    public OptionAdapter(List<OptionItem> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OptionItem item = options.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(item));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.option_title);
            description = itemView.findViewById(R.id.option_description);
        }
    }
}
