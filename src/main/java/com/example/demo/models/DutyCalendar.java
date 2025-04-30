package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "duty_calendar")
@Data
@NoArgsConstructor
public class DutyCalendar {

    @EmbeddedId
    private DutyCalendarId id;

    @Column(name = "is_leave", nullable = false)
    private Boolean isLeave;
}
