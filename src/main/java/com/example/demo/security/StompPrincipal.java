package com.example.demo.security;

import java.security.Principal;

/**
 * 自定义的StompPrincipal，用于在WebSocket中携带用户信息
 */
public class StompPrincipal implements Principal {

    private final String userId;
    private final String username;

    public StompPrincipal(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String getName() {
        return userId;  // Spring Security要求返回一个唯一标识
    }

    public String getUsername() {
        return username;
    }
}
