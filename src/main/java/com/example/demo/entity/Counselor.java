package com.example.demo.entity;

public class Counselor {
    private String id;
    private String dutyArrangement;
    private String institute;
    private String jobTitle;
    private Integer counselorSameTime;
    private String tag;
    private double averageRating;
    private int totalSessions;

    @Override
    public String toString() {
        return "Counselor{" +
                "id='" + id + '\'' +
                ", dutyArrangement='" + dutyArrangement + '\'' +
                ", institute='" + institute + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", counselorSameTime=" + counselorSameTime +
                ", tag='" + tag + '\'' +
                ", averageRating=" + averageRating +
                ", totalSessions=" + totalSessions +
                '}';
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDutyArrangement() {
        return dutyArrangement;
    }

    public void setDutyArrangement(String dutyArrangement) {
        this.dutyArrangement = dutyArrangement;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getCounselorSameTime() {
        return counselorSameTime;
    }

    public void setCounselorSameTime(Integer counselorSameTime) {
        this.counselorSameTime = counselorSameTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}

