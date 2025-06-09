package com.example.educationalchallenge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.adapters.OptionAdapter;
import com.example.educationalchallenge.items.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private RecyclerView optionRecyclerView;
    private OptionAdapter optionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        optionRecyclerView = view.findViewById(R.id.options_recycler);
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OptionItem> options = new ArrayList<>();
        options.add(new OptionItem("Решать квизы", "Проверяй свои знания!"));
        options.add(new OptionItem("Решать судоку", "Тренируй свою логику!"));
        options.add(new OptionItem("Решать кроссворды", "Стань богом эрудиции!"));

        optionAdapter = new OptionAdapter(options, option -> optionTransition(option));

        optionRecyclerView.setAdapter(optionAdapter);

        return view;
    }

    private void optionTransition(OptionItem item) {
        final String title = item.getTitle();
        if (title.equals("Решать квизы")) {

        } else if (title.equals("Решать судоку")) {

        }
    }
}
