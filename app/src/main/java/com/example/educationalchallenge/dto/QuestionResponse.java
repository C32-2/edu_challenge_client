package com.example.educationalchallenge.dto;

import java.io.Serializable;
import java.util.List;

public class QuestionResponse implements Serializable {
    public Long id;
    public String text;
    public Long topicId;
    public List<Answer> answers;
}
