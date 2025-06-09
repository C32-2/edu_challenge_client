package com.example.educationalchallenge.dto;

import java.util.List;

public class QuestionResponse {
    public Long id;
    public String text;
    public Long topicId;
    public List<Answer> answers;
}
