//package com.counseling.platform.controllers;
package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
//import com.counseling.platform.models.User;
//import com.counseling.platform.security.JwtTokenProvider;
//import com.counseling.platform.services.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册和登出
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), 
                            loginRequest.getPassword()
                    )
            );
            
            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = userService.findByUsername(loginRequest.getUsername());
            
            // 生成JWT令牌
            String jwt = jwtTokenProvider.generateToken(user);
            
            // 更新用户状态为在线
            userService.updateUserStatus(user.getId(), User.UserStatus.ONLINE);
            
            // 返回令牌和用户信息
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("tokenType", "Bearer");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Username is already taken");
            }
            
            // 创建新用户
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .nickname(registerRequest.getNickname())
                    .role(User.UserRole.USER) // 默认角色为普通用户
                    .status(User.UserStatus.OFFLINE)
                    .build();
            
            // 保存用户
            User savedUser = userService.createUser(user);
            
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        try {
            if (authentication != null) {
                // 获取用户信息
                User user = userService.findByUsername(authentication.getName());
                
                if (user != null) {
                    // 更新用户状态为离线
                    userService.updateUserStatus(user.getId(), User.UserStatus.OFFLINE);
                }
            }
            
            // 清除认证信息
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        try {
            // 获取用户信息
            User user = userService.findByUsername(authentication.getName());
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to get current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user information: " + e.getMessage());
        }
    }

    /**
     * 登录请求
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 注册请求
     */
    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;
    }
}