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

    /** 当前在线咨询师数量 */
    @GetMapping("/online/counselors")
    public Result onlineCounselors() {
        return Result.success(statisticsService.getOnlineCounselorCount());
    }

    /** 当前在线督导数量 */
    @GetMapping("/online/tutors")
    public Result onlineTutors() {
        return Result.success(statisticsService.getOnlineTutorCount());
    }

    /** 正在进行中的督导‑咨询师一对一指导会话数 */
    @GetMapping("/ongoing-guidance")
    public Result ongoingGuidance() {
        return Result.success(statisticsService.getOngoingGuidanceCount());
    }

    /** 最近 7 天咨询数量折线图数据 */
    @GetMapping("/consult-last7days")
    public Result consultStats7Days() {
        return Result.success(statisticsService.getConsultCountLast7Days());
    }
}
