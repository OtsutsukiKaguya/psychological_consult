package com.example.demo.service.impl;

import com.example.demo.dto.AIDiagnosisRequestDTO;
import com.example.demo.dto.AIDiagnosisResponseDTO;
import com.example.demo.dto.DiagnosisResultDTO;
import com.example.demo.entity.Counselor;
import com.example.demo.entity.DiagnosisLog;
import com.example.demo.mapper.CounselorMapper;
import com.example.demo.mapper.DiagnosisLogMapper;
import com.example.demo.service.AIDiagnosisService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @Value("${model.service.url}")
    private String modelServiceUrl;

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

        // 构造符合 InternLM2.5 聊天格式的 prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("<|im_start|>system\n你是一个温和、专业、善解人意、经验丰富的心理咨询师<|im_end|>\n");

        List<String> history = userConversations.get(userId);
        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) {
                promptBuilder.append("<|im_start|>user\n").append(history.get(i)).append("<|im_end|>\n");
            } else {
                promptBuilder.append("<|im_start|>assistant\n").append(history.get(i)).append("<|im_end|>\n");
            }
        }
        promptBuilder.append("<|im_start|>assistant\n");

        String prompt = promptBuilder.toString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("n_predict", 1024);
        requestBody.put("temperature", 0.7);
        requestBody.put("stop", List.of("<|im_end|>"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String aiReply;
        try {
            String responseJson = restTemplate.postForObject(modelServiceUrl + "/completion", entity, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            aiReply = root.path("content").asText();
        } catch (JsonProcessingException e) {
            aiReply = "AI 响应格式错误，无法解析。";
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

        StringBuilder promptBuilder = new StringBuilder();

// 系统角色定义
        promptBuilder.append("<|im_start|>system\n")
                .append("你是一位经验丰富的心理咨询师，请你根据用户与心理咨询师的对话内容，用心理咨询中常见的专业术语总结用户的心理状态，并输出3~5个心理咨询中常见的专业术语作为标签，描述用户遇到的问题和心理状态。\n")
                .append("你的回答必须严格按照以下格式输出，且不需要任何额外解释：\n")
                .append("总结：XXX。标签：XXX，XXX，XXX。\n")
                .append("<|im_end|>\n");

// 拼接用户当前对话历史
        for (int i = 0; i < session.size(); i++) {
            if (i % 2 == 0) {
                promptBuilder.append("<|im_start|>user\n").append(session.get(i)).append("<|im_end|>\n");
            } else {
                promptBuilder.append("<|im_start|>assistant\n").append(session.get(i)).append("<|im_end|>\n");
            }
        }

// 加入实际提问
        promptBuilder.append("<|im_start|>user\n")
                .append("请使用心理咨询中常见的专业术语对上面的多轮对话总结用户遇到的问题和的心理状态，并使用3~5个心理咨询中常见的专业术语作为标签，描述用户遇到的问题和心理状态。\n")
                .append("请严格遵守格式，仅返回内容，不要额外解释。\n")
                .append("格式如下：\n总结：XXX。标签：XXX，XXX，XXX。\n")
                .append("<|im_end|>\n")
                .append("<|im_start|>assistant\n");

// 构建最终 prompt
        String finalPrompt = promptBuilder.toString();

// 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", finalPrompt);
        requestBody.put("n_predict", 512);  // 可适当增加，避免被截断
        requestBody.put("temperature", 0.7);
        requestBody.put("stop", List.of("<|im_end|>"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String aiReply;
        String diagnosisSummary = "AI 响应异常";
        String cleanedKeywords = "综合";

        try {
            String responseJson = restTemplate.postForObject(modelServiceUrl + "/completion", entity, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            aiReply = root.path("content").asText();
            diagnosisSummary = extractSummary(aiReply);
            cleanedKeywords = extractKeywordsFromModelReply(aiReply);
        } catch (Exception e) {
            aiReply = "AI 响应格式错误，无法解析。";
        }

        // 1. 存日志
        DiagnosisLog log = new DiagnosisLog();
        log.setUserId(userId);
        log.setFullConversation(String.join("\n", session));
        log.setDiagnosisResult(diagnosisSummary);
        log.setKeywordTag(cleanedKeywords);
        log.setCreateTime(LocalDateTime.now());
        diagnosisLogMapper.insert(log);

        // 2. 分词关键词 + 数据库推荐
        Set<Counselor> results = new LinkedHashSet<>();
        List<String> keywords = segmenter.sentenceProcess(cleanedKeywords); // jieba 中文分词
        // 停用词表
        List<String> stopWords = Arrays.asList(
                "的", "是", "了", "在",
                "问题", "关系", "心理", "精神", "情况", "方面", "状态", "表现", "出现"
        );
        for (String keyword : keywords) {
            String trimmed = keyword.trim();
            if (!trimmed.isEmpty() && !stopWords.contains(trimmed)) {
                List<Counselor> matched = counselorMapper.findByTagLike(trimmed);
                if (!matched.isEmpty()) {
                    System.out.println("有效关键词匹配：" + trimmed + " → " + matched.size() + "条");
                }
                results.addAll(matched);
            } else {
                System.out.println("忽略停用词：" + trimmed);
            }
        }

        // 3. 返回推荐结果
        DiagnosisResultDTO dto = new DiagnosisResultDTO();
        dto.setSummary(diagnosisSummary);
        dto.setKeywords(cleanedKeywords);
        dto.setRecommendations(new ArrayList<>(results));
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
