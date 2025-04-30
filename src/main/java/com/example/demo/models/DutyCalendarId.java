package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DutyCalendarId implements Serializable {

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "duty_date")
    private LocalDate dutyDate;
}

