package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class UpdateReservationDTO {
    public String userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime originalTime;

    public String originalCounselorId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime newTime;

    public String newCounselorId;

    public String reservationDescription;
}
