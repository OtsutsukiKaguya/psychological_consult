package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：创建预约 & 查询预约列表")
    void createAndListReservations() {
        // 创建预约
        Map<String,Object> body = Map.of(
                "userId",1,
                "counselorId",2,
                "startTime","2025-05-08T14:00:00",
                "endTime","2025-05-08T15:00:00"
        );
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, h());
        ResponseEntity<Map> resp = restTemplate.postForEntity(url("/api/reservations"), req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?,?> data = (Map<?,?>) resp.getBody().get("data");
        assertThat(data.get("reservationId")).isInstanceOf(Number.class);

        // 查询列表
        ResponseEntity<Map> listResp = restTemplate.getForEntity(url("/api/reservations?userId=1"), Map.class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?,?>)listResp.getBody().get("data")).containsKey("reservations"));
    }
}
