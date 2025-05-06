package com.example.demo.dto;

public class SearchTutorDTO {
    private String id;
    private String role;
    private String counselorId;
    private Integer totalSessions;
    private String dutyArrangement;

    @Override
    public String toString() {
        return "SearchTutorDTO{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", totalSessions=" + totalSessions +
                ", duty_arrangement='" + dutyArrangement + '\'' +
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

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
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
