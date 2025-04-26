package com.example.demo.service.impl;

import com.example.demo.DTO.ChatMessageDTO;
import com.example.demo.models.ChatMessage;
import com.example.demo.service.MessageCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCacheServiceImpl implements MessageCacheService {

    private final RedisTemplate<String, Object> redis;
    private final StringRedisTemplate stringRedisTemplate; // 新加的专用于字符串的redisTemplate
    private static final String KEY_PREFIX = "chat:msg:";   // chat:msg:{sessionId}
    private static final long TTL = 24; // 小时


    @Override
    public void pushMessage(ChatMessage msg) {
        if (msg.getSession() == null || msg.getSession().getId() == null) {
            throw new IllegalArgumentException("ChatMessage session or session.id is null");
        }

        ChatMessageDTO dto = new ChatMessageDTO(
                msg.getSession().getId(),
                msg.getSender().getId(),
                msg.getFile() != null ? msg.getFile().getId() : null,
                msg.getContent(),
                msg.getType().name(),
                msg.getSentAt(),
                msg.isRead()
        );

        String sessionId = dto.getSessionId();
        String key = "chat:msg:" + sessionId;

        redis.opsForList().rightPush(key, dto);
        redis.expire(key, TTL, TimeUnit.HOURS);

        stringRedisTemplate.opsForSet().add("chat:sessions", sessionId);

        // ✅ 日志调试
        log.info("✅ 写入 Redis 消息：sessionId={}, content={}, type={}", sessionId, dto.getContent(), dto.getType());
    }



    @Override
    public List<ChatMessage> getRecent(String sessionId, int limit) {
        String key = KEY_PREFIX + sessionId;
        // 强转前请确保缓存中数据类型正确（即 ChatMessage）
        return (List<ChatMessage>) (Object) redis.opsForList().range(key, -limit, -1);
    }

    @Override
    public List<ChatMessage> popAll(String sessionId) {
        String key = KEY_PREFIX + sessionId;
        List<Object> all = redis.opsForList().range(key, 0, -1);
        redis.delete(key);
        return (List<ChatMessage>) (Object) all;
    }

    @Override
    public List<String> getAllSessionIds() {
        Set<String> sessionIds = stringRedisTemplate.opsForSet().members("chat:sessions");
        return sessionIds != null ? new ArrayList<>(sessionIds) : Collections.emptyList();
    }

}
