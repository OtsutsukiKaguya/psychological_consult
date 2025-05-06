package com.example.demo.dto;

public class TutorHomePageDTO {

    private Integer totalSessions; // 总会话数
    private Integer dailySessions; // 某天会话数
    private Long dailyTotalDuration; // 某天总时长（分钟）
    private Integer ongoingSessions; // 未结束会话数

    @Override
    public String toString() {
        return "TutorHomePageDTO{" +
                "totalSessions=" + totalSessions +
                ", dailySessions=" + dailySessions +
                ", dailyTotalDuration=" + dailyTotalDuration +
                ", ongoingSessions=" + ongoingSessions +
                '}';
    }

    // Getter 和 Setter
    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getDailySessions() {
        return dailySessions;
    }

    public void setDailySessions(Integer dailySessions) {
        this.dailySessions = dailySessions;
    }

    public Long getDailyTotalDuration() {
        return dailyTotalDuration;
    }

    public void setDailyTotalDuration(Long dailyTotalDuration) {
        this.dailyTotalDuration = dailyTotalDuration;
    }

    public Integer getOngoingSessions() {
        return ongoingSessions;
    }

    public void setOngoingSessions(Integer ongoingSessions) {
        this.ongoingSessions = ongoingSessions;
    }
}
