package com.example.demo.service.impl;

import com.example.demo.DTO.ConsultationRankingDTO;
import com.example.demo.DTO.OngoingSessionDTO;
import com.example.demo.models.ChatSession;
import com.example.demo.models.User;
import com.example.demo.repositories.ChatSessionRepository;
import com.example.demo.repositories.DutyCalendarRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.SessionStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SessionStatisticsServiceImpl implements SessionStatisticsService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final DutyCalendarRepository dutyCalendarRepository;


    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOnlineStatusAndOngoingCounts() {
        LocalDate today = LocalDate.now();

        // 1. 查今天有排班且未请假的员工ID
        List<String> staffIdsOnDuty = dutyCalendarRepository.findStaffIdsByDutyDateAndIsLeave(today);

        // 2. 查询这些用户的信息
        List<User> scheduledUsers = userRepository.findAllById(staffIdsOnDuty);

        // 3. 按角色分类
        List<User> counselors = scheduledUsers.stream()
                .filter(u -> u.getRole() == User.UserRole.COUNSELOR)
                .toList();

        List<User> tutors = scheduledUsers.stream()
                .filter(u -> u.getRole() == User.UserRole.TUTOR)
                .toList();

        // 4. 统计进行中会话数
        long counselorOngoing = counselors.stream()
                .mapToLong(c -> chatSessionRepository.findByParticipantsUserId(c.getId())
                        .stream().filter(s -> !Boolean.TRUE.equals(s.getEnded())).count())
                .sum();

        long tutorOngoing = tutors.stream()
                .mapToLong(t -> chatSessionRepository.findByParticipantsUserId(t.getId())
                        .stream().filter(s -> !Boolean.TRUE.equals(s.getEnded())).count())
                .sum();

        // 5. 封装返回
        Map<String, Object> result = new HashMap<>();
        result.put("counselors", counselors);
        result.put("tutors", tutors);
        result.put("counselorOngoingSessions", counselorOngoing);
        result.put("tutorOngoingSessions", tutorOngoing);
        return result;
    }


    //咨询数目排行榜
    @Override
    @Transactional(readOnly = true)
    public List<ConsultationRankingDTO> getConsultationRanking(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = "month".equalsIgnoreCase(period) ? now.withDayOfMonth(1) :
                "year".equalsIgnoreCase(period) ? now.withDayOfYear(1) : null;
        LocalDateTime end = start != null ? ("month".equalsIgnoreCase(period) ? start.plusMonths(1) : start.plusYears(1)) : null;
        if (start == null || end == null) throw new IllegalArgumentException("period must be 'month' or 'year'");

        List<User> counselors = userRepository.findByRole(User.UserRole.COUNSELOR);

        return counselors.stream().map(c -> {
                    int count = (int) chatSessionRepository.findByParticipantsUserId(c.getId()).stream()
                            .filter(s -> s.getCreatedAt().isAfter(start) && s.getCreatedAt().isBefore(end))
                            .count();
                    return new ConsultationRankingDTO(c.getName(), count);
                }).sorted(Comparator.comparingInt(ConsultationRankingDTO::getConsultationCount).reversed())
                .limit(10)  // ✅ 只取前 10 名
                .toList();
    }

    //好评排行榜
    @Override
    @Transactional(readOnly = true)
    public List<ConsultationRankingDTO> getPositiveFeedbackRanking(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = "month".equalsIgnoreCase(period) ? now.withDayOfMonth(1) :
                "year".equalsIgnoreCase(period) ? now.withDayOfYear(1) : null;
        LocalDateTime end = start != null ? ("month".equalsIgnoreCase(period) ? start.plusMonths(1) : start.plusYears(1)) : null;
        if (start == null || end == null) throw new IllegalArgumentException("period must be 'month' or 'year'");

        List<User> counselors = userRepository.findByRole(User.UserRole.COUNSELOR);

        return counselors.stream().map(c -> {
                    int count = (int) chatSessionRepository.findByParticipantsUserId(c.getId()).stream()
                            .filter(s -> s.getRating() != null && s.getRating() == 5)
                            .filter(s -> s.getEndedAt() != null && s.getEndedAt().isAfter(start) && s.getEndedAt().isBefore(end))
                            .count();
                    return new ConsultationRankingDTO(c.getName(), count);
                }).sorted(Comparator.comparingInt(ConsultationRankingDTO::getConsultationCount).reversed())
                .limit(10)  // ✅ 只取前 10 名
                .toList();
    }

    //查询用户目前正在进行的会话
    @Override
    @Transactional(readOnly = true)
    public List<OngoingSessionDTO> findOngoingSessionsByUserId(String userId) {
        List<ChatSession> sessions = chatSessionRepository.findByParticipantsUserId(userId);

        return sessions.stream()
                .filter(session -> !Boolean.TRUE.equals(session.getEnded()))
                .sorted(Comparator.comparing(ChatSession::getUpdatedAt).reversed()) // 🔼 活跃优先
                .map(session -> {
                    List<String> participantIds = session.getParticipants().stream()
                            .map(p -> p.getUser().getId())
                            .toList();

                    return new OngoingSessionDTO(
                            session.getId(),
                            session.getCreatedAt(),
                            session.getType(),
                            participantIds
                    );
                }).toList();
    }

}
