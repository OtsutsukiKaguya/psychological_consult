package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：发送系统通知")
    void sendNotification_success() {
        Map<String,Object> dto = Map.of("userId",2,"title","升级","content","今晚维护");
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(dto, h());
        ResponseEntity<Map> resp = restTemplate.postForEntity(url("/api/notifications/send"), req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().get("message")).isEqualTo("Notification sent");
    }
}