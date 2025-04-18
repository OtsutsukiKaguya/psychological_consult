//package com.counseling.platform.security;
package com.example.demo.security;

//import com.counseling.platform.models.User;
//import com.counseling.platform.services.UserService;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebSocket认证拦截器
 * 用于验证WebSocket连接的认证信息
 */
@Component
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;

    /**
     * 处理入站消息前执行
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 获取STOMP消息头
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 连接时验证JWT令牌
            try {
                // 从请求头中获取令牌
                List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
                if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                    String authorizationHeader = authorizationHeaders.get(0);
                    
                    // 检查是否包含Bearer前缀
                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring(7);
                        
                        // 验证令牌有效性
                        if (jwtTokenProvider.validateToken(token)) {
                            // 获取用户名并更新用户状态
                            String username = jwtTokenProvider.getUsernameFromToken(token);
                            userService.updateUserStatus(username, User.UserStatus.ONLINE);
                            
                            // 获取用户并设置认证信息
                            User user = userService.findById(username);
                            
                            if (user != null) {
                                // 创建认证对象并设置到上下文中
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        username, null, jwtTokenProvider.getAuthorities(token));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                accessor.setUser(authentication);
                                
                                log.debug("User {} connected to WebSocket", username);
                            } else {
                                log.error("User not found: {}", username);
                            }
                        } else {
                            log.error("Invalid JWT token");
                        }
                    } else {
                        log.error("Authorization header does not contain Bearer token");
                    }
                } else {
                    log.error("No Authorization header found");
                }
            } catch (Exception e) {
                log.error("Error during WebSocket authentication", e);
            }
        } else if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            // 断开连接时更新用户状态
            try {
                if (accessor.getUser() != null) {
                    String username = accessor.getUser().getName();
                    userService.updateUserStatus(username, User.UserStatus.OFFLINE);
                    log.debug("User {} disconnected from WebSocket", username);
                }
            } catch (Exception e) {
                log.error("Error during WebSocket disconnection", e);
            }
        }
        
        return message;
    }
}