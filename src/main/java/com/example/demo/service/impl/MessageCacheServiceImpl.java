//package com.example.demo.service.impl;
//
//import com.example.demo.models.ChatMessage;
//import com.example.demo.service.MessageCacheService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class MessageCacheServiceImpl implements MessageCacheService {
//
//    private final RedisTemplate<String, Object> redis;
//    private static final String KEY_PREFIX = "chat:msg:";   // chat:msg:{sessionId}
//    private static final long TTL = 24; // 小时
//
//    @Override
//    public void pushMessage(ChatMessage msg) {
//        String key = KEY_PREFIX + msg.getSessionId();
//        redis.opsForList().rightPush(key, msg);
//        redis.expire(key, TTL, TimeUnit.HOURS);
//    }
//
//    @Override
//    public List<ChatMessage> getRecent(String sessionId, int limit) {
//        String key = KEY_PREFIX + sessionId;
//        return (List<ChatMessage>) (Object) redis.opsForList()
//                .range(key, -limit, -1);
//    }
//
//    @Override
//    public List<ChatMessage> popAll(String sessionId) {
//        String key = KEY_PREFIX + sessionId;
//        List<Object> all = redis.opsForList().range(key, 0, -1);
//        redis.delete(key);
//        return (List<ChatMessage>) (Object) all;
//    }
//}

package com.example.demo.service.impl;

import com.example.demo.models.ChatMessage;
import com.example.demo.service.MessageCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageCacheServiceImpl implements MessageCacheService {

    private final RedisTemplate<String, Object> redis;
    private static final String KEY_PREFIX = "chat:msg:";   // chat:msg:{sessionId}
    private static final long TTL = 24; // 小时

//    @Override
//    public void pushMessage(ChatMessage msg) {
//        if (msg.getSession() == null || msg.getSession().getId() == null) {
//            throw new IllegalArgumentException("ChatMessage session or session.id is null");
//        }
//        String key = KEY_PREFIX + msg.getSession().getId();
//        redis.opsForList().rightPush(key, msg);
//        redis.expire(key, TTL, TimeUnit.HOURS);
//    }

    @Override
    public void pushMessage(ChatMessage msg) {
        if (msg.getSession() == null || msg.getSession().getId() == null) {
            throw new IllegalArgumentException("ChatMessage session or session.id is null");
        }

        String sessionId = msg.getSession().getId();
        String key = KEY_PREFIX + sessionId;

        // 将消息写入 Redis List
        redis.opsForList().rightPush(key, msg);
        redis.expire(key, TTL, TimeUnit.HOURS);

        // ✅ 同时将 sessionId 写入 Redis Set，便于后续调度任务获取所有活跃会话
        redis.opsForSet().add("chat:sessions", sessionId);
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
        Set<Object> sessionIds = redis.opsForSet().members("chat:sessions");
        return sessionIds.stream().map(Object::toString).toList();
    }

}
