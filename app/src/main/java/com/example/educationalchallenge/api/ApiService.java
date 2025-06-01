package com.example.educationalchallenge.api;

import com.example.educationalchallenge.dto.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/users/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("/api/users/register")
    Call<Void> register(@Body RegisterRequest request);

    @GET("/api/users/{userId}")
    Call<ProfileResponse> getProfile(@Path("userId") String userId, @Header("Authorization") String authToken);
}
