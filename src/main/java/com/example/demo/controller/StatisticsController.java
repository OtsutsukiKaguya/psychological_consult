package com.example.demo.controller;

import com.example.demo.service.StatisticsService;
import com.example.demo.common.Result; // 你已有的统一响应类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取今日咨询数量
     */
    @GetMapping("/today/consult-count")
    public Result getTodayConsultCount() {
        int count = statisticsService.getTodayConsultCount();
        return Result.success(count);
    }

    /**
     * 获取今日总咨询时长（HH:mm:ss 格式）
     */
    @GetMapping("/today/consult-duration")
    public Result getTodayConsultDuration() {
        String duration = statisticsService.getTodayConsultDurationFormatted();
        return Result.success(duration);
    }
}
