package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class SessionIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：开始 & 结束咨询会话")
    void startAndEndSession() {
        // 开始
        Map<String,Integer> reqBody = Map.of("userId",1,"counselorId",2);
        HttpEntity<Map<String,Integer>> req = new HttpEntity<>(reqBody, h());
        ResponseEntity<Map> startResp = restTemplate.postForEntity(url("/api/sessions/start"), req, Map.class);
        assertThat(startResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Number sessionId = (Number)((Map<?,?>)startResp.getBody().get("data")).get("sessionId");

        // 结束
        HttpEntity<Void> endReq = new HttpEntity<>(h());
        ResponseEntity<Map> endResp = restTemplate.postForEntity(url("/api/sessions/" + sessionId + "/end"), endReq, Map.class);
        assertThat(endResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(endResp.getBody().get("message")).isEqualTo("Session ended");
    }
}
