package com.example.demo.entity;

import java.time.LocalDate;

public class Duty_calendar {
    private String staffId;
    private LocalDate dutyDate;
    private int isLeave;

    public int getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(int isLeave) {
        this.isLeave = isLeave;
    }

    public LocalDate getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Override
    public String toString() {
        return "Duty_calendar{" +
                "staffId='" + staffId + '\'' +
                ", dutyDate=" + dutyDate +
                ", isLeave=" + isLeave +
                '}';
    }
}
