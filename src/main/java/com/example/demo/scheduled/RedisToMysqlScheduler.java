package com.example.demo.scheduled;

import com.example.demo.models.ChatMessage;
import com.example.demo.repositories.ChatMessageRepository;
import com.example.demo.service.MessageCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisToMysqlScheduler {

    private final MessageCacheService messageCacheService;
    private final ChatMessageRepository chatMessageRepository;

    @Scheduled(fixedRate = 30000)
    public void persistCachedMessages() {
        for (String sessionId : messageCacheService.getAllSessionIds()) {
            List<ChatMessage> messages = messageCacheService.popAll(sessionId);
            if (messages != null && !messages.isEmpty()) {
                chatMessageRepository.saveAll(messages);
                log.info("✅ 持久化 Redis 中 {} 条消息 到 MySQL - session: {}", messages.size(), sessionId);
            }
        }
    }
}
