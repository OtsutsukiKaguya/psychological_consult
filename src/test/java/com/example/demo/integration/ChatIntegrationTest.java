package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ChatIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：发送 & 获取聊天消息")
    void sendAndFetchChat() {
        // 发送消息
        Map<String,Object> msg = Map.of("sessionId",1,"senderId",1,"content","hello");
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(msg, h());
        ResponseEntity<Map> sendResp = restTemplate.postForEntity(url("/api/chat/send"), req, Map.class);
        assertThat(sendResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?,?>)sendResp.getBody().get("data")).get("messageId")).isInstanceOf(Number.class);

        // （可选）获取历史消息
        ResponseEntity<Map> listResp = restTemplate.getForEntity(url("/api/chat/history?sessionId=1"), Map.class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?,?>)listResp.getBody().get("data")).containsKey("messages"));
    }
}
