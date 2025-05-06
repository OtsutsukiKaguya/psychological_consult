package com.example.demo.controller;

import com.example.demo.dto.TreeHolePostDTO;
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
class TreeHoleControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("发布树洞帖子返回 201")
    void postTreeHole_success() throws Exception {
        TreeHolePostDTO dto = new TreeHolePostDTO();
        dto.setUserId(1L);
        dto.setContent("今天心情不好，想倾诉。");
        mvc.perform(post("/api/treehole/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.postId").isNumber());
    }

    @Test
    @DisplayName("回复树洞帖子返回 200")
    void replyTreeHole_success() throws Exception {
        Map<String,Object> body = Map.of("userId",1L,"content","我理解你","postId",5L);
        mvc.perform(post("/api/treehole/posts/5/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.replyId").isNumber());
    }
}
