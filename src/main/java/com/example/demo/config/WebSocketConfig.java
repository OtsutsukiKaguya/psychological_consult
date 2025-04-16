//package com.counseling.platform.config;
package com.example.demo.config;

import com.example.demo.security.WebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置
 * 配置WebSocket连接、消息代理和频道
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * 配置WebSocket端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 用户订阅点，前缀为/topic和/queue
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 发送到应用的消息前缀为/app
        registry.setApplicationDestinationPrefixes("/app");
        
        // 设置单用户订阅前缀
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 配置通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 添加WebSocket认证拦截器
        registration.interceptors(webSocketAuthInterceptor);
    }
}