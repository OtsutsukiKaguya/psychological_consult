package com.example.demo.dto;

public class MoodDeleteRequest {
    private String userId;
    private String moodDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(String moodDate) {
        this.moodDate = moodDate;
    }

    @Override
    public String toString() {
        return "MoodDeleteRequest{" +
                "userId='" + userId + '\'' +
                ", moodDate='" + moodDate + '\'' +
                '}';
    }

}
