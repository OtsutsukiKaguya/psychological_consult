package com.example.demo.controller;

import com.example.demo.dto.DiagnosisRequestDTO;
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
class InitialDiagnosisControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("AI 初诊返回推荐咨询师列表")
    void aiDiagnosis_success() throws Exception {
        DiagnosisRequestDTO dto = new DiagnosisRequestDTO();
        dto.setUserId(1L);
        dto.setAnswers(Map.of("q1","a","q2","b"));
        mvc.perform(post("/api/diagnosis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }
}