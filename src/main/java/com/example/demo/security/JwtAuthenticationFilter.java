//package com.counseling.platform.security;
package com.example.demo.security;

//import com.counseling.platform.services.UserService;

import com.example.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于验证请求中的JWT令牌
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求中获取JWT令牌
            String token = jwtTokenProvider.resolveToken(request);
            
            // 检查令牌是否有效
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 从令牌中获取认证信息
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                
                // 设置认证信息到上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 更新用户的最后活动时间（可选）
                String username = jwtTokenProvider.getUsernameFromToken(token);
                userService.findById(username); // 触发更新
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }
        
        filterChain.doFilter(request, response);
    }
}