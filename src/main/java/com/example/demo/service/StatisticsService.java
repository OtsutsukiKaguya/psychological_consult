package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    int getTodayConsultCount();
    String getTodayConsultDurationFormatted();
    int  getOnlineCounselorCount();
    int  getOnlineTutorCount();
    int  getOngoingGuidanceCount();
    List<Map<String,Object>> getConsultCountLast7Days();
}
