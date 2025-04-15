package com.example.demo.dto;

public class AIDiagnosisRequestDTO {
    private String userId;
    private String userInput;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
