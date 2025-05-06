package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("发送文本消息返回 200")
    void sendTextMessage_success() throws Exception {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSessionId(1L);
        dto.setSenderId(1L);
        dto.setContent("你好，心理师！");
        mvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.messageId").isNumber());
    }

    @Test
    @DisplayName("发送空消息返回 400")
    void sendEmptyMessage_badRequest() throws Exception {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSessionId(1L);
        dto.setSenderId(1L);
        dto.setContent("");
        mvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
