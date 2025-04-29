package com.example.demo.security;

import com.example.demo.models.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class); // ✅ 更安全的写法

        if (accessor != null) {
            log.info("📩 接收到STOMP命令: {}", accessor.getCommand());
        }

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔐 开始进行WebSocket连接认证...");

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null) {
                log.error("❌ 没有找到Authorization头");
                throw new AccessDeniedException("Missing Authorization header");
            }

            if (!authHeader.startsWith("Bearer ")) {
                log.error("❌ Authorization头格式错误，应以Bearer开头");
                throw new AccessDeniedException("Invalid Authorization header format");
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                log.error("❌ JWT Token验证失败");
                throw new AccessDeniedException("Invalid JWT token");
            }

            String userId = jwtTokenProvider.getUserIdFromToken(token);

            if (userId == null) {
                log.error("❌ Token中提取不到userId");
                throw new AccessDeniedException("Invalid JWT token content");
            }

            // ✅ 关键！强制绑定用户到accessor
            accessor.setUser(new StompPrincipal(userId));
            log.info("✅ WebSocket连接认证通过，绑定用户userId: {}", userId);

            userService.updateUserStatus(userId, User.UserStatus.ONLINE);
            log.info("✅ 用户{} 状态更新为ONLINE", userId);
        }

        else if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("🔌 检测到WebSocket断开连接...");

            Principal principal = accessor.getUser();
            if (principal != null) {
                String userId = principal.getName();
                userService.updateUserStatus(userId, User.UserStatus.OFFLINE);
                log.info("✅ 用户{} 状态更新为OFFLINE", userId);
            } else {
                log.warn("⚠️ 断开连接时找不到用户信息");
            }
        }

        return message;
    }

    public static class StompPrincipal implements Principal {
        private final String userId;

        public StompPrincipal(String userId) {
            this.userId = userId;
        }

        @Override
        public String getName() {
            return userId;
        }
    }
}
