package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class UserIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("集成：用户注册与登录流程")
    void registerAndLogin_flow() {
        // 注册
        Map<String, String> reg = Map.of("phone","13800000003","password","Pass@123");
        HttpEntity<Map<String,String>> req = new HttpEntity<>(reg, headers());
        ResponseEntity<Map> regResp = restTemplate.postForEntity(url("/api/users/register"), req, Map.class);
        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<?,?> data = (Map<?,?>) regResp.getBody().get("data");
        assertThat(data.get("userId")).isInstanceOf(Number.class);

        // 登录
        Map<String, String> login = Map.of("phone","13800000003","password","Pass@123");
        HttpEntity<Map<String,String>> loginReq = new HttpEntity<>(login, headers());
        ResponseEntity<Map> loginResp = restTemplate.postForEntity(url("/api/users/login"), loginReq, Map.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?,?> loginData = (Map<?,?>) loginResp.getBody().get("data");
        assertThat(loginData.get("token")).isNotNull();
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}