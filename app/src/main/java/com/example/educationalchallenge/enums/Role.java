package com.example.educationalchallenge.enums;

public enum Role {
    USER,
    EDITOR,
    ADMIN;

    public static Role fromString(String roleString) {
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
