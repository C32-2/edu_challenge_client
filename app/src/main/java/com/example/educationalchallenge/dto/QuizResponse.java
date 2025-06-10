package com.example.educationalchallenge.dto;

import java.io.Serializable;
import java.util.List;

public class QuizResponse implements Serializable {
    public Long id;
    public String title;
    public int topicId;
    public String createdAt;
    public List<QuestionResponse> questions;
}