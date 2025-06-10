package com.example.educationalchallenge.api;

import com.example.educationalchallenge.dto.*;
import com.example.educationalchallenge.enums.Role;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @POST("/api/users/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("/api/users/register")
    Call<Void> register(@Body RegisterRequest request);

    @GET("/api/users/{userId}")
    Call<ProfileResponse> getProfile(@Path("userId") String userId, @Header("Authorization") String token);

    @POST("/api/topics")
    Call<Void> addTopic(@Body AddTopicRequest request, @Header("Authorization") String token);

    @GET("/api/topics")
    Call<List<TopicResponse>> getAllTopics(@Header("Authorization") String token);

    @POST("/api/questions")
    Call<Void> addQuestion(@Body AddQuestionRequest request, @Header("Authorization") String token);

    @GET("/api/questions")
    Call<List<QuestionResponse>> searchQuestion(
            @Query("topicId") Long topicId,
            @Query("query") String query,
            @Header("Authorization") String token
    );

    @POST("/api/quizzes")
    Call<Void> addQuiz(@Body AddQuizRequest request, @Header("Authorization") String token);

    @GET("/api/quizzes")
    Call<List<QuizResponse>> getQuizzes(
            @Query("topicId") Long topicId,
            @Header("Authorization") String token
    );

    @GET("/api/users/leaderboard")
    Call<List<ProfileResponse>> getLeaderboard(@Header("Authorization") String token);

    @GET("/api/users")
    Call<List<ProfileResponse>> getAllUsers(@Header("Authorization") String token);

    @POST("/api/users/change-role")
    Call<Void> changeRole(
            @Query("id") Long id,
            @Query("role") Role role,
            @Header("Authorization") String token
    );
}
