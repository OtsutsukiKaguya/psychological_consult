package com.example.demo.entity;

import java.time.LocalDateTime;

public class Duty_calendar {
    private String staffId;
    private LocalDateTime dutyDate;

    public LocalDateTime getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(LocalDateTime dutyDate) {
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
        return "dutyCalendar{" +
                "staffId='" + staffId + '\'' +
                ", dutyDate=" + dutyDate +
                '}';
    }

}
