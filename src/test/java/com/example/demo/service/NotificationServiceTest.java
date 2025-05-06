package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository repo;
    @InjectMocks NotificationService service;

    @Test
    @DisplayName("发送通知——保存并返回 ID")
    void sendNotification_savesAndReturns() {
        Notification n = new Notification();
        n.setUserId(2L);
        n.setTitle("Title");
        n.setContent("Content");

        Notification saved = new Notification();
        saved.setId(400L);
        when(repo.save(any())).thenReturn(saved);

        Notification result = service.send(n);
        assertEquals(400L, result.getId());
        verify(repo).save(any(Notification.class));
    }
}

