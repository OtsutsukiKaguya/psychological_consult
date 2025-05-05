package com.example.demo.service.impl;

import com.example.demo.mapper.StatisticsMapper;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
