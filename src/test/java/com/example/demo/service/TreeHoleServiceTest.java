package com.example.demo.service;

import com.example.demo.entity.TreeHolePost;
import com.example.demo.repository.TreeHoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class TreeHoleServiceTest {

    @Mock TreeHoleRepository repo;
    @InjectMocks TreeHoleService service;

    @Test
    @DisplayName("新帖发布——保存并返回 ID")
    void postToTreeHole_savesAndReturns() {
        TreeHolePost p = new TreeHolePost();
        p.setUserId(1L);
        p.setContent("content");

        TreeHolePost saved = new TreeHolePost();
        saved.setId(300L);
        when(repo.save(any())).thenReturn(saved);

        TreeHolePost result = service.post(p);
        assertEquals(300L, result.getId());
        verify(repo).save(any(TreeHolePost.class));
    }

    @Test
    @DisplayName("回复帖子——调用 save")
    void replyToTreeHole_callsSave() {
        // 假设 reply 方法内部调用 save
        TreeHolePost reply = service.reply(300L, 1L, "reply");
        verify(repo).save(any(TreeHolePost.class));
        assertNotNull(reply);
    }
}
