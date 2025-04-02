package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Reservation {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationTime;

    private String userId;
    private String counselorId;
    private String reservationDescription;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getReservationDescription() {
        return reservationDescription;
    }

    public void setReservationDescription(String reservationDescription) {
        this.reservationDescription = reservationDescription;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "userId='" + userId + '\'' +
                ", reservationTime=" + reservationTime +
                ", counselorId='" + counselorId + '\'' +
                ", reservationDescription='" + reservationDescription + '\'' +
                '}';
    }

}
