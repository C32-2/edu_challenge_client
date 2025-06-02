package com.example.educationalchallenge.dto;

public class TopicResponse {
    public int id;
    public String name;

    @Override
    public String toString() {
        return id + ". " + name;
    }
}
