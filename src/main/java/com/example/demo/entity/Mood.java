package com.example.demo.entity;

import java.time.LocalDate;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Mood {
    private String userId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moodDate; // 改为 LocalDate
    private String moodType;
    private String moodContent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(LocalDate moodDate) {
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
