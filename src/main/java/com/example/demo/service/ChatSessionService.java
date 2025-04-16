//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.ChatSession;
//import com.counseling.platform.models.SessionParticipant;
//import com.counseling.platform.models.User;
import com.example.demo.models.ChatSession;
import com.example.demo.models.SessionParticipant;
import com.example.demo.models.User;

import java.util.List;

/**
 * 聊天会话服务接口
 */
public interface ChatSessionService {

    /**
     * 创建会话
     */
    ChatSession createSession(ChatSession session);

    /**
     * 根据ID查找会话
     */
    ChatSession findById(Long id);

    /**
     * 获取所有会话
     */
    List<ChatSession> findAll();

    /**
     * 根据用户ID查找会话
     */
    List<ChatSession> findByUserId(Long userId);

    /**
     * 获取两个用户之间的一对一会话
     */
    ChatSession getOneToOneSession(Long userId1, Long userId2);

    /**
     * 更新会话信息
     */
    ChatSession updateSession(Long id, ChatSession session);

    /**
     * 删除会话
     */
    void deleteSession(Long id);

    /**
     * 添加会话参与者
     */
    SessionParticipant addParticipant(Long sessionId, Long userId, SessionParticipant.ParticipantRole role);

    /**
     * 移除会话参与者
     */
    void removeParticipant(Long sessionId, Long userId);

    /**
     * 获取会话参与者
     */
    List<User> getSessionParticipants(Long sessionId);

    /**
     * 获取会话参与者（包含角色信息）
     */
    List<SessionParticipant> getSessionParticipantsWithRoles(Long sessionId);

    /**
     * 检查用户是否是会话参与者
     */
    boolean isSessionParticipant(Long sessionId, Long userId);

    /**
     * 获取用户在某会话中的角色
     */
    SessionParticipant.ParticipantRole getUserRoleInSession(Long sessionId, Long userId);

    /**
     * 标记会话为已读
     */
    void markSessionAsRead(Long sessionId, Long userId);

    /**
     * 获取用户的最近会话
     */
    List<ChatSession> getRecentSessions(Long userId);

    /**
     * 获取未读消息数
     */
    int getUnreadMessageCount(Long sessionId, Long userId);

    /**
     * 更新会话最后活动时间
     */
    void updateLastActivity(Long sessionId);
}