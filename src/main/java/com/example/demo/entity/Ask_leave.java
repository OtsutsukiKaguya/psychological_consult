package com.example.demo.entity;

public class Ask_leave {
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(String dutyTime) {
        this.dutyTime = dutyTime;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }

    public String getLeaveComment() {
        return leaveComment;
    }

    public void setLeaveComment(String leaveComment) {
        this.leaveComment = leaveComment;
    }

    private String staffId;
    private String dutyTime; //修改
    private String leaveReason;
    private Boolean isAgree;
    private String leaveComment;

    @Override
    public String toString() {
        return "Ask_leave{" +
                "staffId='" + staffId + '\'' +
                ", dutyTime='" + dutyTime + '\'' +
                ", leaveReason='" + leaveReason + '\'' +
                ", isAgree=" + isAgree +
                ", leaveComment='" + leaveComment + '\'' +
                '}';
    }

}
