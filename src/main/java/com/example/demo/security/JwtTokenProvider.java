//package com.counseling.platform.security;
package com.example.demo.security;

//import com.counseling.platform.models.User;

import com.example.demo.models.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT令牌提供者
 * 用于生成、验证和解析JWT令牌
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:counseling-platform-secret-key}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private int jwtExpiration;

    /**
     * 从用户信息生成JWT令牌
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        
        // 获取用户权限
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从用户实体生成JWT令牌
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        return Jwts.builder()
                .setSubject(user.getName())
                .claim("roles", "ROLE_" + user.getRole().name())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从JWT令牌获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }

    /**
     * 从JWT令牌获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("userId", Long.class);
    }

    /**
     * 从JWT令牌获取授权信息
     */
    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        
        return Arrays.stream(claims.get("roles").toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }

    /**
     * 从请求中解析JWT令牌
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从令牌获取认证信息
     */
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Collection<? extends GrantedAuthority> authorities = getAuthorities(token);
        
        // 创建认证对象
        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }
}