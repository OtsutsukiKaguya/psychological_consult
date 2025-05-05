package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StatisticsMapper {

    // 今日咨询数量
    @Select("SELECT COUNT(*) FROM chat_sessions WHERE ended = 1 AND DATE(ended_at) = CURDATE()")
    int getTodayConsultCount();

    // 今日总咨询时长（单位：秒）
    @Select("SELECT COALESCE(SUM(TIMESTAMPDIFF(SECOND, created_at, ended_at)), 0) FROM chat_sessions WHERE ended = 1 AND DATE(ended_at) = CURDATE()")
    long getTodayConsultDurationSeconds();
}
