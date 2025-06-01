package com.example.educationalchallenge.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

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

    public static String getIdFromToken(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_WRAP);

            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(decodedPayload);

            if (jsonObject.has("userId")) {
                return String.valueOf(jsonObject.getInt("userId"));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRoleFromToken(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_WRAP);

            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(decodedPayload);

            if (jsonObject.has("role")) {
                return String.valueOf(jsonObject.getString("role"));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
 }
