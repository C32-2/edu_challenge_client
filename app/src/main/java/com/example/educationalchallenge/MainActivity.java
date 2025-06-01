package com.example.educationalchallenge;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.educationalchallenge.fragments.EditorFragment;
import com.example.educationalchallenge.fragments.MainFragment;
import com.example.educationalchallenge.fragments.ProfileFragment;
import com.example.educationalchallenge.fragments.StatisticsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigationItemSelected(item));
    }

    private boolean handleNavigationItemSelected(MenuItem item) {
        Fragment currentFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            currentFragment = new MainFragment();
        } else if (id == R.id.nav_statistics) {
            currentFragment = new StatisticsFragment();
        }
        else if (id == R.id.nav_editors) {
            currentFragment = new EditorFragment();
        }
        else if (id == R.id.nav_profile) {
            currentFragment = new ProfileFragment();
        }

        if (currentFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
            getSupportActionBar().setTitle(item.getTitle());
            return true;
        }
        return false;
    }
}