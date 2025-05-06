package com.example.demo.dto;

import java.time.LocalDate;

public class MoodUpsertRequestDTO {
    private String userId;
    private LocalDate moodDate;
    private String moodType;
    private String moodContent;

    // Getter / Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getMoodDate() { return moodDate; }
    public void setMoodDate(LocalDate moodDate) { this.moodDate = moodDate; }

    public String getMoodType() { return moodType; }
    public void setMoodType(String moodType) { this.moodType = moodType; }

    public String getMoodContent() { return moodContent; }
    public void setMoodContent(String moodContent) { this.moodContent = moodContent; }
}