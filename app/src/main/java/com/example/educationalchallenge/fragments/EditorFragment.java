package com.example.educationalchallenge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.AddTopicActivity;
import com.example.educationalchallenge.R;
import com.example.educationalchallenge.adapters.OptionAdapter;
import com.example.educationalchallenge.models.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class EditorFragment extends Fragment {

    private RecyclerView optionRecyclerView;
    private OptionAdapter optionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        optionRecyclerView = view.findViewById(R.id.options_recycler);
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OptionItem> options = new ArrayList<>();
        options.add(new OptionItem("Добавить топик для квиза", "Любая тема"));
        options.add(new OptionItem("Добавить вопрос в базу", "С учетом выбранной темы"));
        options.add(new OptionItem("Составить квиз", "Составляй квизы из вопросов из базы"));
        options.add(new OptionItem("Назначить редактора", "Строго для администратора"));

        optionAdapter = new OptionAdapter(options, option -> optionTransition(option));

        optionRecyclerView.setAdapter(optionAdapter);

        return view;
    }

    private void optionTransition(OptionItem item) {
        final String title = item.getTitle();
        if (title.equals("Добавить топик для квиза")) {
            Intent intent = new Intent(requireContext(), AddTopicActivity.class);
            startActivity(intent);
        } else if (title.equals("Добавить вопрос в базу")) {

        } else if (title.equals("Составить квиз")) {

        } else if (title.equals("Назначить редактора")) {

        }
    }
}
