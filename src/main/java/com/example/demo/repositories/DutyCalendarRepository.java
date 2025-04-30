package com.example.demo.repositories;

import com.example.demo.models.DutyCalendar;
import com.example.demo.models.DutyCalendarId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DutyCalendarRepository extends JpaRepository<DutyCalendar, DutyCalendarId> {

    @Query("SELECT d.id.staffId FROM DutyCalendar d WHERE d.id.dutyDate = :date AND d.isLeave = false")
    List<String> findStaffIdsByDutyDateAndIsLeave(@Param("date") LocalDate date);
}
