package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserService userService;

    @Test
    @DisplayName("注册新用户——调用 Repository 并返回实体")
    void registerUser_savesAndReturnsUser() {
        User input = new User();
        input.setPhone("13800000002");
        input.setPassword("pass");
        User saved = new User();
        saved.setId(10L);
        saved.setPhone(input.getPhone());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.register(input);
        assertEquals(10L, result.getId());
        assertEquals("13800000002", result.getPhone());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("登录——正确密码返回用户，错误密码抛异常")
    void login_checksPassword() {
        User stored = new User();
        stored.setPhone("13800000002");
        stored.setPassword("encoded");
        when(userRepository.findByPhone("13800000002")).thenReturn(java.util.Optional.of(stored));
        // 假设 passwordEncoder 直接匹配
        doReturn(true).when(userService).checkPassword("raw", "encoded");

        User result = userService.login("13800000002", "raw");
        assertSame(stored, result);

        doReturn(false).when(userService).checkPassword("wrong", "encoded");
        assertThrows(RuntimeException.class,
                () -> userService.login("13800000002", "wrong"));
    }
}

