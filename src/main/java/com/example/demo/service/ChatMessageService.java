//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.ChatMessage;
import com.example.demo.models.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * 聊天消息服务接口
 */
public interface ChatMessageService {

    /**
     * 创建消息
     */
    ChatMessage createMessage(ChatMessage message);

    /**
     * 根据ID查找消息
     */
    ChatMessage findById(Integer id);

    /**
     * 获取会话消息
     */
    List<ChatMessage> getSessionMessages(String sessionId, int limit, int offset);

    /**
     * 获取会话中指定类型的消息
     */
    List<ChatMessage> getSessionMessagesByType(String sessionId, ChatMessage.MessageType type);

    /**
     * 获取会话最后一条消息
     */
    ChatMessage getLastMessage(String sessionId);

    /**
     * 根据关键字搜索消息
     */
    List<ChatMessage> searchMessages(String sessionId, String keyword);

    /**
     * 标记消息为已读
     */
    void markMessageAsRead(Integer messageId, String userId);

    /**
     * 标记会话所有消息为已读
     */
    void markSessionMessagesAsRead(String sessionId, String userId);

    /**
     * 检查消息是否已读
     */
    boolean isMessageRead(Integer messageId, String userId);

    /**
     * 获取未读消息数
     */
    int getUnreadCount(String sessionId, String userId);

    /**
     * 获取用户的所有未读消息
     */
    List<ChatMessage> getAllUnreadMessages(String userId);

    /**
     * 删除消息
     */
    void deleteMessage(Integer id);

    /**
     * 删除会话的所有消息
     */
    void deleteSessionMessages(String sessionId);

    //这是我为了先解决报错随便创建的save方法，后面应该要改
    ChatMessage save(ChatMessage message);
}