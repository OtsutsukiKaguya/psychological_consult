package com.example.demo.service;

import com.example.demo.DTO.ConsultationRankingDTO;
import com.example.demo.DTO.OngoingSessionDTO;

import java.util.List;
import java.util.Map;

public interface SessionStatisticsService {

    /**
     * 获取在线咨询师和督导的状态以及进行中的会话数量
     */
    Map<String, Object> getOnlineStatusAndOngoingCounts();

    /**
     * 获取咨询量排行榜（按月或年）
     * @param period 取值为 \"month\" 或 \"year\"
     */
    List<ConsultationRankingDTO> getConsultationRanking(String period);

    /**
     * 获取好评排行榜（按月或年）
     * @param period 取值为 \"month\" 或 \"year\"
     */
    List<ConsultationRankingDTO> getPositiveFeedbackRanking(String period);

    //获取正在进行的会话
    List<OngoingSessionDTO> findOngoingSessionsByUserId(String userId);
}
