package com.example.demo.service.impl;

import com.example.demo.dto.AIDiagnosisRequestDTO;
import com.example.demo.dto.AIDiagnosisResponseDTO;
import com.example.demo.dto.DiagnosisResultDTO;
import com.example.demo.entity.Counselor;
import com.example.demo.entity.DiagnosisLog;
import com.example.demo.mapper.CounselorMapper;
import com.example.demo.mapper.DiagnosisLogMapper;
import com.example.demo.service.AIDiagnosisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIDiagnosisServiceImpl implements AIDiagnosisService {

    private final Map<String, List<String>> userConversations = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final DiagnosisLogMapper diagnosisLogMapper;
    private final CounselorMapper counselorMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JiebaSegmenter segmenter = new JiebaSegmenter();

    @Value("${ecnu.api.url}")
    private String ecnuApiUrl;

    @Value("${ecnu.api.token}")
    private String ecnuApiToken;

    public AIDiagnosisServiceImpl(DiagnosisLogMapper diagnosisLogMapper, CounselorMapper counselorMapper) {
        this.diagnosisLogMapper = diagnosisLogMapper;
        this.counselorMapper = counselorMapper;
    }

    @Override
    public AIDiagnosisResponseDTO getDiagnosis(AIDiagnosisRequestDTO request) {
        String userId = request.getUserId();
        String userInput = request.getUserInput();

        if (userInput.length() > 500) {
            userInput = userInput.substring(0, 500);
        }

        userConversations.computeIfAbsent(userId, k -> new ArrayList<>()).add(userInput);
        List<String> history = userConversations.get(userId);

        // 构造 messages 列表
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", "你是一个温和、专业、善解人意、经验丰富的心理咨询师"
        ));
        for (int i = 0; i < history.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messages.add(Map.of(
                    "role", role,
                    "content", history.get(i)
            ));
        }

        // 请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "ecnu-max");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ecnuApiToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String aiReply;
        try {
            String responseJson = restTemplate.postForObject(ecnuApiUrl, entity, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            aiReply = root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            aiReply = "AI 响应异常，无法解析。";
        }

        userConversations.get(userId).add(aiReply);

        AIDiagnosisResponseDTO response = new AIDiagnosisResponseDTO();
        response.setDiagnosisResult(aiReply);
        return response;
    }

    @Override
    public DiagnosisResultDTO finishAndRecommend(String userId) {
        List<String> session = userConversations.get(userId);
        if (session == null || session.isEmpty()) {
            DiagnosisResultDTO empty = new DiagnosisResultDTO();
            empty.setSummary("无对话内容");
            empty.setKeywords("无");
            empty.setRecommendations(Collections.emptyList());
            return empty;
        }

        // 构造 messages 列表
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", "你是一位经验丰富的心理咨询师，请你根据用户与心理咨询师的对话内容，用心理咨询中常见的专业术语总结用户的心理状态，并输出3~5个心理咨询中常见的专业术语作为标签。输出格式：总结：XXX。标签：XXX，XXX，XXX。"
        ));
        for (int i = 0; i < session.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messages.add(Map.of(
                    "role", role,
                    "content", session.get(i)
            ));
        }
        messages.add(Map.of(
                "role", "user",
                "content", "请严格按照格式：总结：XXX。标签：XXX，XXX，XXX，不要额外解释。"
        ));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "ecnu-max");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ecnuApiToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String diagnosisSummary = "AI 响应异常";
        String cleanedKeywords = "综合";
        try {
            String responseJson = restTemplate.postForObject(ecnuApiUrl, entity, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            String aiReply = root.path("choices").get(0).path("message").path("content").asText();
            diagnosisSummary = extractSummary(aiReply);
            cleanedKeywords = extractKeywordsFromModelReply(aiReply);

            // 存日志
            DiagnosisLog log = new DiagnosisLog();
            log.setUserId(userId);
            log.setFullConversation(String.join("\n", session));
            log.setDiagnosisResult(diagnosisSummary);
            log.setKeywordTag(cleanedKeywords);
            log.setCreateTime(LocalDateTime.now());
            diagnosisLogMapper.insert(log);
        } catch (Exception e) {
            // 忽略，使用默认值
        }

        // 关键词匹配咨询师推荐
        Set<Counselor> results = new LinkedHashSet<>();
        List<String> keywords = segmenter.sentenceProcess(cleanedKeywords);
        List<String> stopWords = Arrays.asList("的", "是", "了", "在", "问题", "关系", "心理", "情况");
        for (String kw : keywords) {
            String trimmed = kw.trim();
            if (!trimmed.isEmpty() && !stopWords.contains(trimmed)) {
                results.addAll(counselorMapper.findByTagLike(trimmed));
            }
        }

        DiagnosisResultDTO dto = new DiagnosisResultDTO();
        dto.setSummary(diagnosisSummary);
        dto.setKeywords(cleanedKeywords);
        dto.setRecommendations(new ArrayList<>(results));
        //新增：清空该用户的历史上下文
        userConversations.remove(userId);
        return dto;
    }

    private String extractSummary(String reply) {
        Pattern pattern = Pattern.compile("总结[:：]\\s*(.+?)(。|\\n|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(reply);
        return matcher.find() ? matcher.group(1).trim() : reply;
    }

    private String extractKeywordsFromModelReply(String reply) {
        Pattern pattern = Pattern.compile("标签[:：]\\s*(.+?)(。|\\n|$)");
        Matcher matcher = pattern.matcher(reply);
        return matcher.find() ? matcher.group(1).trim() : reply;
    }
}
