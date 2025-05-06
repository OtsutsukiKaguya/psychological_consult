package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class TreeHoleIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private String url(String p) { return "http://localhost:" + port + p; }
    private HttpHeaders h() { HttpHeaders hh = new HttpHeaders(); hh.setContentType(MediaType.APPLICATION_JSON); return hh; }

    @Test
    @DisplayName("集成：发布 & 回复树洞帖子")
    void postAndReplyTreeHole() {
        // 发布
        Map<String,Object> post = Map.of("userId",1,"content","test post");
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(post, h());
        ResponseEntity<Map> postResp = restTemplate.postForEntity(url("/api/treehole/posts"), req, Map.class);
        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number postId = (Number)((Map<?,?>)postResp.getBody().get("data")).get("postId");

        // 回复
        Map<String,Object> reply = Map.of("userId",1,"content","reply","postId", postId);
        HttpEntity<Map<String,Object>> repReq = new HttpEntity<>(reply, h());
        ResponseEntity<Map> repResp = restTemplate.postForEntity(url("/api/treehole/posts/" + postId + "/reply"), repReq, Map.class);
        assertThat(repResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?,?>)repResp.getBody().get("data")).get("replyId")).isInstanceOf(Number.class);
    }
}