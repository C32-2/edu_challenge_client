package com.example.educationalchallenge.security;

import android.content.Context;
import android.content.SharedPreferences;

public class JwtManager {

    private final String PREFS_NAME = "jwt_prefs";
    private final String TOKEN_KEY = "jwt_token";
    private SharedPreferences sharedPreferences;

    public JwtManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }
 }
