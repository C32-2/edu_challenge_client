package com.example.educationalchallenge.dto;

import java.util.List;

public class AddQuizRequest {
    private String title;
    private Long topicId;
    private List<Long> questionIds;
    public AddQuizRequest(String title, Long topicId, List<Long> questionIds) {
        this.title = title;
        this.topicId = topicId;
        this.questionIds = questionIds;
    }
}
