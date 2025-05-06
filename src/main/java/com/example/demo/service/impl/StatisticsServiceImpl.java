package com.example.demo.service.impl;

import com.example.demo.mapper.StatisticsMapper;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public int getTodayConsultCount() {
        return statisticsMapper.getTodayConsultCount();
    }

    @Override
    public String getTodayConsultDurationFormatted() {
        long totalSeconds = statisticsMapper.getTodayConsultDurationSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override public int  getOnlineCounselorCount()     { return statisticsMapper.getOnlineCounselorCount(); }
    @Override public int  getOnlineTutorCount()         { return statisticsMapper.getOnlineTutorCount(); }
    @Override public int  getOngoingGuidanceCount()     { return statisticsMapper.getOngoingGuidanceCount(); }
    @Override public List<Map<String,Object>> getConsultCountLast7Days() {
        return statisticsMapper.getConsultCountLast7Days();
    }
}
