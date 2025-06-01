package com.example.educationalchallenge.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.ProfileResponse;
import com.example.educationalchallenge.security.JwtManager;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, nicknameTextView, roleTextView, levelTextView,
            idTextView, quizzesSolved, createdAtTextView;
    private ProgressBar expProgressBar;
    private Button editProfileButton;

    private JwtManager jwtManager;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация вьюшек
        usernameTextView = view.findViewById(R.id.username_text);
        nicknameTextView = view.findViewById(R.id.nickname_text);
        roleTextView = view.findViewById(R.id.role_text);
        levelTextView = view.findViewById(R.id.level_exp_text);
        expProgressBar = view.findViewById(R.id.exp_progress);
        idTextView = view.findViewById(R.id.user_id_text);
        quizzesSolved = view.findViewById(R.id.quizzes_solved_text);
        editProfileButton = view.findViewById(R.id.change_button);
        createdAtTextView = view.findViewById(R.id.created_date_text);

        setViewsVisibility(false);
        expProgressBar.setVisibility(View.VISIBLE);

        jwtManager = new JwtManager(getContext());
        apiService = ApiClient.getApiService();

        String jwt = jwtManager.getToken();
        if (jwt == null || jwt.isEmpty()) {
            Toast.makeText(getContext(), "Токен не найден!", Toast.LENGTH_SHORT).show();
            return view;
        }

        String userId = JwtManager.getIdFromToken(jwt);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "ID пользователя не найден!", Toast.LENGTH_SHORT).show();
            return view;
        }

        Call<ProfileResponse> call = apiService.getProfile(userId, "Bearer " + jwt);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                expProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    setUserData(response.body());
                } else {
                    Log.e("ProfileFragment", "Ошибка ответа: " + response.code());
                    Toast.makeText(getContext(), "Ошибка при загрузке данных!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                expProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка при загрузке данных!", Toast.LENGTH_SHORT).show();
                Log.e("ProfileFragment", "Ошибка при запросе профиля", t);
            }
        });

        return view;
    }

    private void setUserData(ProfileResponse response) {
        usernameTextView.setText(response.username != null ? response.username : "");
        nicknameTextView.setText("Новичок");
        roleTextView.setText("Роль: " + (response.role != null ? response.role : ""));
        levelTextView.setText("Уровень " + response.level + ", Опыт " + response.exp);
        createdAtTextView.setText("Создан: " + (response.createdAt != null ? response.createdAt : ""));
        idTextView.setText("ID Пользователя: " + response.id);
        quizzesSolved.setText("Решено квизов: " + response.quizzesSolved);

        setViewsVisibility(true);
    }

    private void setViewsVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        usernameTextView.setVisibility(visibility);
        nicknameTextView.setVisibility(visibility);
        roleTextView.setVisibility(visibility);
        levelTextView.setVisibility(visibility);
        idTextView.setVisibility(visibility);
        quizzesSolved.setVisibility(visibility);
        editProfileButton.setVisibility(visibility);
        createdAtTextView.setVisibility(visibility);
    }
}
