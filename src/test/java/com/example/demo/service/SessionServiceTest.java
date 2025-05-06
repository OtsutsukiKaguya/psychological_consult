package com.example.demo.service;

import com.example.demo.entity.ChatSession;
import com.example.demo.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock SessionRepository sessionRepository;
    @InjectMocks SessionService sessionService;

    @Test
    @DisplayName("启动会话——保存并返回 sessionId")
    void startSession_savesAndReturns() {
        ChatSession s = new ChatSession();
        s.setUserId(1L);
        s.setCounselorId(2L);

        ChatSession saved = new ChatSession();
        saved.setId(50L);
        when(sessionRepository.save(any())).thenReturn(saved);

        ChatSession result = sessionService.startSession(1L, 2L);
        assertEquals(50L, result.getId());
        verify(sessionRepository).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("结束会话——更新状态")
    void endSession_updatesStatus() {
        ChatSession existing = new ChatSession();
        existing.setId(1L);
        existing.setStatus("ACTIVE");
        when(sessionRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existing));

        sessionService.endSession(1L);
        assertEquals("ENDED", existing.getStatus());
        verify(sessionRepository).save(existing);
    }
}