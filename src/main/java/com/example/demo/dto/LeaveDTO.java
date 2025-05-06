package com.example.demo.dto;

import java.time.LocalDate;

public class LeaveDTO {

    private String role;
    private String staffId;
    private LocalDate dutyDate;
    private String leaveReason;
    private Boolean isAgree;
    private String leaveComment;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public LocalDate getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Boolean getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(Boolean isAgree) {
        this.isAgree = isAgree;
    }

    public String getLeaveComment() {
        return leaveComment;
    }

    public void setLeaveComment(String leaveComment) {
        this.leaveComment = leaveComment;
    }

    @Override
    public String toString() {
        return "LeaveDTO{" +
                "role='" + role + '\'' +
                ", staffId=" + staffId +
                ", dutyDate=" + dutyDate +
                ", leaveReason='" + leaveReason + '\'' +
                ", isAgree=" + isAgree +
                ", leaveComment='" + leaveComment + '\'' +
                '}';
    }
}
