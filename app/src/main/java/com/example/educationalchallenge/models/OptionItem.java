package com.example.educationalchallenge.models;

public class OptionItem {
    private String title;
    private String description;

    public OptionItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
