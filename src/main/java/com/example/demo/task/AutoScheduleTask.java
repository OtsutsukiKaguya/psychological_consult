package com.example.demo.task;

import com.example.demo.service.DutyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AutoScheduleTask {

    @Autowired
    private DutyScheduleService scheduleService;

    /** 每天凌晨1点自动生成未来7天的排班 */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateNextWeek() {
        LocalDate today = LocalDate.now();
        scheduleService.generateSchedule(today.plusDays(1), today.plusDays(7), 5);
    }
}
