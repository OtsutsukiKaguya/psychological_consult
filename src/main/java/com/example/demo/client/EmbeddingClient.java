package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmbeddingClient {
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/embeddings";
    private static final String API_KEY = "9bdfee948bd2c66391ce98846c8eb11f.DAwakyeia0Ck9Bz8";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EmbeddingClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> recommend(String userText, List<String> tags, List<String> ids) {
        List<String> input = new ArrayList<>();
        input.add(userText);
        input.addAll(tags);

        Map<String, Object> payload = Map.of(
                "model", "embedding-3",
                "input", input
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, HttpMethod.POST, entity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            List<double[]> embeddings = new ArrayList<>();
            for (JsonNode item : data) {
                double[] vec = objectMapper.convertValue(item.get("embedding"), double[].class);
                embeddings.add(vec);
            }

            double[] userVec = embeddings.get(0);
            List<ScoredCounselor> scored = new ArrayList<>();
            for (int i = 0; i < tags.size(); i++) {
                double[] counselorVec = embeddings.get(i + 1);
                double sim = cosineSimilarity(userVec, counselorVec);
                scored.add(new ScoredCounselor(ids.get(i), sim));
            }

            return scored.stream()
                    .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                    .limit(5)
                    .map(c -> c.id)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("❌ embedding 调用失败：" + e.getMessage());
            return ids.subList(0, Math.min(5, ids.size())); // fallback
        }
    }

    private static class ScoredCounselor {
        String id;
        double similarity;

        ScoredCounselor(String id, double similarity) {
            this.id = id;
            this.similarity = similarity;
        }
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
