package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.DTO.ConsultationRankingDTO;
import com.example.demo.DTO.OngoingSessionDTO;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.SessionStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final SessionStatisticsService statisticsService;
    private final ChatSessionService chatSessionService;

    /**
     * 获取咨询师/督导在线状态及会话数
     */
    @GetMapping("/online-status")
    public Result getOnlineStatus() {
        Map<String, Object> result = statisticsService.getOnlineStatusAndOngoingCounts();
        return Result.success(result);
    }

    /**
     * 获取咨询数量排行榜
     */
    @GetMapping("/consultation-ranking")
    public Result getConsultationRanking(@RequestParam String period) {
        List<ConsultationRankingDTO> ranking = statisticsService.getConsultationRanking(period);
        return Result.success(ranking);
    }

    /**
     * 获取好评排行榜
     */
    @GetMapping("/positive-feedback-ranking")
    public Result getPositiveFeedbackRanking(@RequestParam String period) {
        List<ConsultationRankingDTO> ranking = statisticsService.getPositiveFeedbackRanking(period);
        return Result.success(ranking);
    }

    /**
     * 获取某用户正在进行的会话列表
     */
    @GetMapping("/ongoing-sessions")
    public Result getOngoingSessions(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return Result.error("User not authenticated");
        }

        String userId = authentication.getName();
        List<OngoingSessionDTO> sessions = statisticsService.findOngoingSessionsByUserId(userId);
        return Result.success(sessions);
    }
}
