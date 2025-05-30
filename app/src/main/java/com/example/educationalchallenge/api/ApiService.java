package com.example.educationalchallenge.api;

import com.example.educationalchallenge.dto.AuthRequest;
import com.example.educationalchallenge.dto.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/users/login")
    Call<AuthResponse> login(@Body AuthRequest request);
}
