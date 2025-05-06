package com.example.demo.controller;

import com.example.demo.dto.UpdateReservationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("正常预约应返回 200 和预约编号")
    void createReservation_success() throws Exception {
        String body = "{\"userId\":1,\"counselorId\":2,\"startTime\":\"2025-05-08T14:00:00\",\"endTime\":\"2025-05-08T15:00:00\"}";
        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservationId").isNumber());
    }

    @Test
    @DisplayName("缺失字段应返回 400")
    void createReservation_missingFields() throws Exception {
        String body = "{\"userId\":1}";
        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("越权修改应返回 403")
    void updateReservation_forbidden() throws Exception {
        UpdateReservationDTO dto = new UpdateReservationDTO();
        dto.setReservationId(123L);
        dto.setComment("修改时间段");
        mvc.perform(put("/api/reservations/123")
                        .header("Authorization", "Bearer fakeTokenOfOtherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}