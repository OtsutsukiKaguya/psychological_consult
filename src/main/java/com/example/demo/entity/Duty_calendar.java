package com.example.demo.entity;

import java.time.LocalDate;

public class Duty_calendar {
    private String staffId;
    private LocalDate dutyDate;

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
        return "dutyCalendar{" +
                "staffId='" + staffId + '\'' +
                ", dutyDate=" + dutyDate +
                '}';
    }

}
