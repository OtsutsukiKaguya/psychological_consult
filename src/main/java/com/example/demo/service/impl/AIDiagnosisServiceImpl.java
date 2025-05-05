package com.example.demo.service.impl;

import com.example.demo.client.EmbeddingClient;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final EmbeddingClient embeddingClient;  // 新增字段

    @Value("${ecnu.api.url}")
    private String ecnuApiUrl;

    @Value("${ecnu.api.token}")
    private String ecnuApiToken;

    public AIDiagnosisServiceImpl(DiagnosisLogMapper diagnosisLogMapper, CounselorMapper counselorMapper, EmbeddingClient embeddingClient) {
        this.diagnosisLogMapper = diagnosisLogMapper;
        this.counselorMapper = counselorMapper;
        this.embeddingClient = embeddingClient;
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
    @Transactional
    public DiagnosisResultDTO finishAndRecommend(String userId) {
        List<String> session = userConversations.get(userId);
        if (session == null || session.isEmpty()) {
            DiagnosisResultDTO empty = new DiagnosisResultDTO();
            empty.setSummary("无对话内容");
            empty.setKeywords("无");
            empty.setRecommendations(Collections.emptyList());
            return empty;
        }

        // 构建 AI 消息
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "你是一位经验丰富的心理咨询师，请你根据用户与心理咨询师的对话内容，" +
                        "用心理咨询中常见的专业术语总结用户的心理状态，并输出3~5个心理咨询中常见的专业术语作为标签。" +
                        "输出格式：总结：XXX。标签：XXX，XXX，XXX。"));
        for (int i = 0; i < session.size(); i++) {
            messages.add(Map.of("role", (i % 2 == 0) ? "user" : "assistant", "content", session.get(i)));
        }
        messages.add(Map.of("role", "user", "content",
                "请严格按照格式：总结：XXX。标签：XXX，XXX，XXX，不要额外解释。"));

        // 调用 AI 模型获取总结与标签
        String diagnosisSummary = "AI 响应异常";
        String cleanedKeywords = "综合";
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "ecnu-max");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(ecnuApiToken);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String responseJson = restTemplate.postForObject(ecnuApiUrl, entity, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            String aiReply = root.path("choices").get(0).path("message").path("content").asText();
            diagnosisSummary = extractSummary(aiReply);
            cleanedKeywords = extractKeywordsFromModelReply(aiReply);

            // 存入初诊日志
            DiagnosisLog log = new DiagnosisLog();
            log.setUserId(userId);
            log.setFullConversation(String.join("\n", session));
            log.setDiagnosisResult(diagnosisSummary);
            log.setKeywordTag(cleanedKeywords);
            log.setCreateTime(LocalDateTime.now());
            diagnosisLogMapper.insert(log);

        } catch (Exception e) {
            // 捕获异常但不中断服务流程
        }

        // 关键词分词并清洗（停用词过滤）
        List<String> rawKeywords = segmenter.sentenceProcess(cleanedKeywords);
        List<String> stopWords = Arrays.asList("的", "是", "了", "在", "问题", "关系", "心理", "情况");
        Set<String> validKeywords = rawKeywords.stream()
                .map(String::trim)
                .filter(k -> !k.isEmpty() && !stopWords.contains(k))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 使用 embedding 微服务推荐匹配咨询师
        List<Counselor> allCounselors = counselorMapper.findAll();
        List<String> counselorIds = allCounselors.stream().map(Counselor::getId).toList();
        List<String> counselorTags = allCounselors.stream().map(Counselor::getTag).toList();

        // 调用嵌入式推荐服务（此处 embeddingClient 是已注入的组件）
        List<String> recommendedIds = embeddingClient.recommend(
                String.join(" ", validKeywords), counselorTags, counselorIds
        );
        List<Counselor> unordered = counselorMapper.findByIds(recommendedIds);
        Map<String, Counselor> counselorMap = unordered.stream()
                .collect(Collectors.toMap(Counselor::getId, c -> c));

        List<Counselor> recommendedCounselors = recommendedIds.stream()
                .limit(5)
                .map(counselorMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        // 构造返回结果
        DiagnosisResultDTO dto = new DiagnosisResultDTO();
        dto.setSummary(diagnosisSummary);
        dto.setKeywords(cleanedKeywords);
        dto.setRecommendations(recommendedCounselors);

        // 清空用户上下文
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