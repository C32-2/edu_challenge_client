package com.example.educationalchallenge;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.educationalchallenge.enums.Role;
import com.example.educationalchallenge.fragments.*;
import com.example.educationalchallenge.security.JwtManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Role role;
    private JwtManager jwtManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupJwtAndRole();
        setupBottomNavigation();

        loadFragment(new MainFragment());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupJwtAndRole() {
        jwtManager = new JwtManager(this);
        String token = jwtManager.getToken();
        String roleString = JwtManager.getRoleFromToken(token);
        role = Role.fromString(roleString);
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            selectedFragment = new MainFragment();
        } else if (id == R.id.nav_statistics) {
            selectedFragment = new StatisticsFragment();
        } else if (id == R.id.nav_editors) {
            if (role != Role.USER) {
                selectedFragment = new EditorFragment();
            } else {
                Toast.makeText(this, "У вас недостаточно прав!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (id == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void loadFragment(Fragment fragment, int titleResId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }
    }
}