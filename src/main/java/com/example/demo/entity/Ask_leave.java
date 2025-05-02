package com.example.demo.entity;
import java.time.LocalDate;

public class Ask_leave {

    private String staffId;
    private LocalDate dutyDate; //修改
    private String leaveReason;
    private Boolean isAgree;
    private String leaveComment;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public LocalDate getdutyDate() {
        return dutyDate;
    }

    public void setdutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Boolean getisAgree() {
        return isAgree;
    }

    public void setisAgree(Boolean isAgree) {
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
        return "Ask_leave{" +
                "staffId='" + staffId + '\'' +
                ", dutyDate='" + dutyDate + '\'' +
                ", leaveReason='" + leaveReason + '\'' +
                ", isAgree=" + isAgree +
                ", leaveComment='" + leaveComment + '\'' +
                '}';
    }

}
