package com.example.demo.scheduled;

import com.example.demo.DTO.ChatMessageDTO;
import com.example.demo.models.ChatMessage;
import com.example.demo.models.ChatSession;
import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.repositories.ChatMessageRepository;
import com.example.demo.service.MessageCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.service.ChatSessionService;
import com.example.demo.service.UserService;
import com.example.demo.service.FileService;

import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisToMysqlScheduler {

    private final MessageCacheService messageCacheService;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper; // ✅ 添加这个就不会报错了
    private final RedisTemplate<String, Object> redis; // ✅ RedisTemplate
    private final ChatSessionService chatSessionService; // ✅ 查 session
    private final UserService userService;               // ✅ 查 user
    private final FileService fileService;               // ✅ 查 file


    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void persistCachedMessages() {
        log.info("🚀 开始执行 Redis → MySQL 消息持久化任务");

        for (String sessionId : messageCacheService.getAllSessionIds()) {
            String key = "chat:msg:" + sessionId;

            List<Object> rawList = redis.opsForList().range(key, 0, -1);
            redis.delete(key); // 删除 Redis 消息列表
            log.info("🧹 Redis 清除 key: {}, 条数: {}", key, rawList != null ? rawList.size() : 0);

            if (rawList == null || rawList.isEmpty()) continue;

            List<ChatMessage> messages = new ArrayList<>();

            for (Object o : rawList) {
                ChatMessageDTO dto;
                try {
                    // ✅ 强制转换 LinkedHashMap → ChatMessageDTO
                    dto = objectMapper.convertValue(o, ChatMessageDTO.class);
                } catch (Exception e) {
                    log.error("❌ Redis中数据无法转换为 ChatMessageDTO: {}, 类型为: {}", o, o.getClass().getName(), e);
                    continue;
                }

                try {
                    ChatSession session = chatSessionService.findById(dto.getSessionId());
                    User sender = userService.findById(dto.getSenderId());
                    File file = dto.getFileId() != null ? fileService.getFile(dto.getFileId()) : null;

                    ChatMessage msg = ChatMessage.builder()
                            .session(session)
                            .sender(sender)
                            .file(file)
                            .content(dto.getContent())
                            .type(ChatMessage.MessageType.valueOf(dto.getType()))
                            .sentAt(dto.getSentAt())
                            .read(dto.isRead())
                            .build();

                    messages.add(msg);
                } catch (Exception e) {
                    log.error("❌ 构建 ChatMessage 实体出错: sessionId={}, senderId={}, err={}",
                            dto.getSessionId(), dto.getSenderId(), e.getMessage());
                }
            }

            if (!messages.isEmpty()) {
                chatMessageRepository.saveAll(messages);
                log.info("✅ 成功持久化 {} 条消息到数据库，sessionId={}", messages.size(), sessionId);
            }
        }
    }
}
