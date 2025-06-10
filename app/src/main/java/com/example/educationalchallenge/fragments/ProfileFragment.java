package com.example.educationalchallenge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.educationalchallenge.R;
import com.example.educationalchallenge.StartActivity;
import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.ProfileResponse;
import com.example.educationalchallenge.security.JwtManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, nicknameTextView, roleTextView, levelTextView,
            idTextView, quizzesSolved, createdAtTextView;
    private ProgressBar expProgressBar, loadProgressBar;
    private Button editProfileButton, logoutButton;
    private JwtManager jwtManager;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);

        setViewsVisibility(false);

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

        loadProfile(userId, jwt);

        logoutButton.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void initViews(View view) {
        usernameTextView = view.findViewById(R.id.username_text);
        nicknameTextView = view.findViewById(R.id.nickname_text);
        roleTextView = view.findViewById(R.id.role_text);
        levelTextView = view.findViewById(R.id.level_exp_text);
        expProgressBar = view.findViewById(R.id.exp_progress);
        idTextView = view.findViewById(R.id.user_id_text);
        quizzesSolved = view.findViewById(R.id.quizzes_solved_text);
        editProfileButton = view.findViewById(R.id.change_button);
        createdAtTextView = view.findViewById(R.id.created_date_text);
        loadProgressBar = view.findViewById(R.id.load_progress_bar);
        logoutButton = view.findViewById(R.id.logout_button);
    }

    private void loadProfile(String userId, String jwt) {
        Call<ProfileResponse> call = apiService.getProfile(userId, "Bearer " + jwt);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                loadProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    setUserData(response.body());
                } else {
                    Toast.makeText(getContext(), "Ошибка при загрузке данных!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                loadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка при загрузке данных!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserData(ProfileResponse response) {
        usernameTextView.setText(response.username != null ? response.username : "");
        nicknameTextView.setText("Новичок");
        roleTextView.setText("Роль: " + (response.role != null ? response.role : ""));
        levelTextView.setText("Уровень " + response.level + ", Опыт " + response.exp);
        createdAtTextView.setText("Создан: " + formatDate(response.createdDate));
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
        expProgressBar.setVisibility(visibility);
        logoutButton.setVisibility(visibility);
    }

    private String formatDate(String rawDate) {
        LocalDateTime dateTime = LocalDateTime.parse(rawDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));
        return dateTime.format(formatter);
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null);

        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        Button btnCancel = dialogView.findViewById(R.id.button_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.button_ok);

        title.setText("Выход из аккаунта");
        message.setText("Вы уверены, что хотите продолжить?");

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            jwtManager.clearToken();
            Intent intent = new Intent(requireContext(), StartActivity.class);
            startActivity(intent);
            dialog.dismiss();
            requireActivity().finish();
        });

        dialog.show();
    }
}