package com.example.educationalchallenge.items;

public class AnswerItem {
    private String text;
    private boolean isCorrect;

    public AnswerItem() {
        this.text = "";
        this.isCorrect = false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
 }
