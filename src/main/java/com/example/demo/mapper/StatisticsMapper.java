package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {

    // 今日咨询数量
    @Select("SELECT COUNT(*) FROM chat_sessions WHERE ended = 1 AND DATE(ended_at) = CURDATE()")
    int getTodayConsultCount();

    // 今日总咨询时长（单位：秒）
    @Select("SELECT COALESCE(SUM(TIMESTAMPDIFF(SECOND, created_at, ended_at)), 0) FROM chat_sessions WHERE ended = 1 AND DATE(ended_at) = CURDATE()")
    long getTodayConsultDurationSeconds();

    /** 在线咨询师数量 */
    @Select("""
        SELECT COUNT(*) FROM person
        WHERE role = 'COUNSELOR' AND state = 'ONLINE'
    """)
    int getOnlineCounselorCount();

    /** 在线督导数量 */
    @Select("""
        SELECT COUNT(*) FROM person
        WHERE role = 'TUTOR' AND state = 'ONLINE'
    """)
    int getOnlineTutorCount();

    /** 正在进行中的督导‑咨询师一对一指导会话数 */
    @Select("""
        SELECT COUNT(*) FROM chat_sessions
        WHERE session_type = 'ONE_TO_ONE' AND ended = 0
    """)
    int getOngoingGuidanceCount();

    /** 最近 7 天（含今天）每日咨询数量 */
    @Select("""
        SELECT DATE(ended_at) AS day,
               COUNT(*)         AS cnt
          FROM chat_sessions
         WHERE ended = 1
           AND DATE(ended_at) >= DATE_SUB(CURDATE(),INTERVAL 6 DAY)
         GROUP BY DATE(ended_at)
         ORDER BY day
    """)
    List<Map<String,Object>> getConsultCountLast7Days();
}
