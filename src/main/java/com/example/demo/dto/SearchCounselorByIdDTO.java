package com.example.demo.dto;

public class SearchCounselorByIdDTO {
    private String userId;

    // Getter 和 Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SearchCounselorByIdDTO{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
