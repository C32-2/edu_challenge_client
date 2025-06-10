package com.example.educationalchallenge.fragments;

import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.adapters.OptionAdapter;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.ProfileResponse;
import com.example.educationalchallenge.items.OptionItem;
import com.example.educationalchallenge.security.JwtManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardFragment extends Fragment {
    private RecyclerView leaderboardRecyclerView;
    private List<ProfileResponse> leaderboard;
    private JwtManager jwtManager;
    private ApiService apiService;
    private OptionAdapter optionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        jwtManager = new JwtManager(getContext());
        apiService = ApiClient.getApiService();

        leaderboardRecyclerView = view.findViewById(R.id.leaderboardRecyclerView);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        leaderboard = new ArrayList<>();
        optionAdapter = new OptionAdapter(new ArrayList<>(), option -> {});
        leaderboardRecyclerView.setAdapter(optionAdapter);

        fetchLeaderboard();

        return view;
    }

    private void fetchLeaderboard() {
        apiService.getLeaderboard("Bearer " + jwtManager.getToken())
                .enqueue(new Callback<List<ProfileResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProfileResponse>> call, Response<List<ProfileResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            leaderboard = response.body();

                            // Обновляем данные адаптера и уведомляем RecyclerView
                            List<OptionItem> optionItems = leaderboard.stream()
                                    .map(profile ->
                                            new OptionItem(
                                                    profile.username,
                                                    "Уровень "+ profile.level.toString()))
                                    .collect(Collectors.toList());

                            optionAdapter.updateData(optionItems);
                        } else {
                            Toast.makeText(requireContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProfileResponse>> call, Throwable t) {
                        Toast.makeText(requireContext(), "Не удалось получить список пользователей!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
