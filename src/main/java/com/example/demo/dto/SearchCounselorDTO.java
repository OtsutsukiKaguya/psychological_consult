package com.example.demo.dto;

public class SearchCounselorDTO {
    private String id;
    private String role;
    private String tutorId;
    private Double averageRating;
    private Integer totalSessions;
    private String dutyArrangement;

    @Override
    public String toString() {
        return "SearchCounselorDTO{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", tutorId='" + tutorId + '\'' +
                ", averageRating=" + averageRating +
                ", totalSessions=" + totalSessions +
                ", dutyArrangement='" + dutyArrangement + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getDutyArrangement() {
        return dutyArrangement;
    }

    public void setDutyArrangement(String dutyArrangement) {
        this.dutyArrangement = dutyArrangement;
    }
}
