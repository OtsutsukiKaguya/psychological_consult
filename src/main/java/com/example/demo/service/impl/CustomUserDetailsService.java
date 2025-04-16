//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

//import com.counseling.platform.models.User;
//import com.counseling.platform.repositories.UserRepository;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService，用于加载用户信息
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            log.error("未找到用户: {}", username);
            throw new UsernameNotFoundException("未找到用户: " + username);
        }
        
        return createUserDetails(user);
    }

    /**
     * 创建Spring Security需要的UserDetails对象
     */
    private UserDetails createUserDetails(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // 添加基于角色的权限
        String role = user.getRole().name();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        
        // 根据角色添加特定权限
        switch (user.getRole()) {
            case COUNSELOR:
                authorities.add(new SimpleGrantedAuthority("PERM_COUNSELING"));
                break;
            case SUPERVISOR:
                authorities.add(new SimpleGrantedAuthority("PERM_COUNSELING"));
                authorities.add(new SimpleGrantedAuthority("PERM_SUPERVISE"));
                break;
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("PERM_COUNSELING"));
                authorities.add(new SimpleGrantedAuthority("PERM_SUPERVISE"));
                authorities.add(new SimpleGrantedAuthority("PERM_MANAGE_USERS"));
                break;
            default:
                // 普通用户不添加额外权限
                break;
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}