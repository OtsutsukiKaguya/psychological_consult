package com.example.demo.service;

import com.example.demo.models.ChatMessage;
import java.util.List;

public interface MessageCacheService {
    /** 向 Redis List 推送一条消息 */
    void pushMessage(ChatMessage msg);

    /** 取出会话最新 N 条消息 */
    List<ChatMessage> getRecent(String sessionId, int limit);

    /** 批量弹出并返回所有缓存消息，用于落库 */
    List<ChatMessage> popAll(String sessionId);

    List<String> getAllSessionIds();
}