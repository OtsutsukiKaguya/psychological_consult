package com.example.demo.controller;

import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.LoginDTO;
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
class UserControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("用户注册成功返回 201")
    void register_success() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setPhone("13800000001");
        dto.setPassword("Pass@123");
        mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").isNumber());
    }

    @Test
    @DisplayName("登录失败—密码错误返回 401")
    void login_wrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setPhone("13800000001");
        dto.setPassword("WrongPass");
        mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("获取用户信息成功返回 200")
    void getProfile_success() throws Exception {
        mvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value("13800000001"));
    }
}
