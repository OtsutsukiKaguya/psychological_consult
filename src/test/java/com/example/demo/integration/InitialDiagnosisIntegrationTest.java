package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class InitialDiagnosisIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：AI 初诊 & 推荐咨询师")
    void diagnosis_recommendCounselors() {
        Map<String,Object> reqBody = Map.of("userId",1,"answers",Map.of("q1","a","q2","b"));
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(reqBody, h());
        ResponseEntity<Map> resp = restTemplate.postForEntity(url("/api/diagnosis"), req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?,?>)resp.getBody().get("data")).get("recommendations")).isInstanceOfAny(java.util.List.class);
    }
}