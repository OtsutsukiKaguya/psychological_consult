package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Mood {
    private String userId;
    private LocalDateTime moodDate;
    private String moodType;
    private String moodContent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(LocalDateTime moodDate) {
        this.moodDate = moodDate;
    }

    public String getMoodType() {
        return moodType;
    }

    public void setMoodType(String moodType) {
        this.moodType = moodType;
    }

    public String getMoodContent() {
        return moodContent;
    }

    public void setMoodContent(String moodContent) {
        this.moodContent = moodContent;
    }


    @Override
    public String toString() {
        return "Mood{" +
                "userId='" + userId + '\'' +
                ", moodDate=" + moodDate +
                ", moodType='" + moodType + '\'' +
                ", moodContent='" + moodContent + '\'' +
                '}';
    }


}
