package com.example.educationalchallenge.dto;

import com.example.educationalchallenge.items.AnswerItem;

import java.util.List;

public class AddQuestionRequest {
    private String text;
    private Long topicId;
    private List<AnswerItem> answers;

    public AddQuestionRequest(String text, Long topicId, List<AnswerItem> answers) {
        this.text = text;
        this.topicId = topicId;
        this.answers = answers;
    }
}
