package com.example.educationalchallenge;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educationalchallenge.api.ApiClient;
import com.example.educationalchallenge.api.ApiService;
import com.example.educationalchallenge.dto.ProfileResponse;
import com.example.educationalchallenge.enums.Role;
import com.example.educationalchallenge.security.JwtManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeRoleActivity extends AppCompatActivity {
    private List<ProfileResponse> users;
    private AutoCompleteTextView userSearch;
    private Long currentUserId = null;
    private Spinner roleSpinner;
    private Button changeRoleButton;
    private ApiService apiService;
    private JwtManager jwtManager;
    private Role selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_role);

        apiService = ApiClient.getApiService();
        jwtManager = new JwtManager(this);

        initViews();
        setupListeners();
        fetchUsers();
        setupRoleSpinner();
    }

    private void initViews() {
        userSearch = findViewById(R.id.userSearch);
        roleSpinner = findViewById(R.id.roleSpinner);
        changeRoleButton = findViewById(R.id.changeRoleButton);
    }

    private void setupListeners() {
        changeRoleButton.setOnClickListener(v -> {
            if (currentUserId == null) {
                Toast.makeText(this, "Выберите пользователя", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedRole == null) {
                Toast.makeText(this, "Выберите роль", Toast.LENGTH_SHORT).show();
                return;
            }
            changeRole(currentUserId, selectedRole);
        });
    }

    private void fetchUsers() {
        apiService.getAllUsers("Bearer " + jwtManager.getToken())
                .enqueue(new Callback<List<ProfileResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProfileResponse>> call, Response<List<ProfileResponse>> response) {
                        if (response.isSuccessful()) {
                            users = response.body();
                            setupUserSearch(users);
                        } else {
                            Toast.makeText(ChangeRoleActivity.this, "Ошибка при загрузке пользователей", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProfileResponse>> call, Throwable t) {
                        Toast.makeText(ChangeRoleActivity.this, "Не удалось загрузить пользователей", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupUserSearch(List<ProfileResponse> users) {
        // Чтобы AutoComplete показывал имя пользователя, создаем список строк и мапим для выбора id
        List<String> usernames = new ArrayList<>();
        for (ProfileResponse user : users) {
            usernames.add(user.username);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                usernames
        );
        userSearch.setAdapter(adapter);
        userSearch.setThreshold(1);

        userSearch.setOnItemClickListener((parent, view, position, id) -> {
            String usernameSelected = (String) parent.getItemAtPosition(position);
            // Ищем id по username
            for (ProfileResponse user : users) {
                if (user.username.equals(usernameSelected)) {
                    currentUserId = user.id;
                    break;
                }
            }
        });
    }

    private void setupRoleSpinner() {
        List<Role> roles = Arrays.asList(Role.USER, Role.EDITOR);

        ArrayAdapter<Role> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = roles.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }
        });
    }

    private void changeRole(Long userId, Role role) {
        for (ProfileResponse user : users) {
            if (user.id.equals(userId) && user.role.equals(Role.ADMIN.name())) {
                Toast.makeText(this, "Нельзя менять роль администратора", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        apiService.changeRole(userId, role, "Bearer " + jwtManager.getToken())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChangeRoleActivity.this, "Роль успешно изменена", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangeRoleActivity.this, "Недостаточно прав", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ChangeRoleActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}