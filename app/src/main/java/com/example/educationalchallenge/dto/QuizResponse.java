package com.example.educationalchallenge.dto;

import java.util.List;

public class QuizResponse {
    public Long id;
    public String title;
    public int topicId;
    public String createdAt;
    public List<QuestionResponse> questions;
}