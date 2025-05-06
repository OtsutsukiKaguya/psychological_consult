package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock ChatRepository chatRepository;
    @InjectMocks ChatService chatService;

    @Test
    @DisplayName("发送消息——保存并返回带 ID 的消息")
    void sendMessage_savesAndReturns() {
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(1L);
        msg.setSenderId(1L);
        msg.setContent("hello");

        ChatMessage saved = new ChatMessage();
        saved.setId(200L);
        when(chatRepository.save(any())).thenReturn(saved);

        ChatMessage result = chatService.send(msg);
        assertEquals(200L, result.getId());
        verify(chatRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("验证空内容抛 400 异常")
    void sendEmpty_throwsBadRequest() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> chatService.send(msg));
        assertTrue(ex.getMessage().contains("Content cannot be empty"));
    }
}