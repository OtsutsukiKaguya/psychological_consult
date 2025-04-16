//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.User;
import com.example.demo.models.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID查找用户
     */
    User findById(Long id);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 获取所有用户
     */
    List<User> findAll();

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(User.UserRole role);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 更新用户信息
     */
    User updateUser(Long id, User user);

    /**
     * 更新用户状态
     */
    User updateUserStatus(Long id, User.UserStatus status);

    /**
     * 根据用户名更新用户状态
     */
    User updateUserStatusByUsername(String username, String status);

    /**
     * 更新用户头像
     */
    User updateUserAvatar(Long id, String avatarUrl);

    /**
     * 获取咨询师的督导
     */
    User getSupervisor(Long counselorId);

    /**
     * 获取督导的咨询师
     */
    List<User> getSupervisees(Long supervisorId);

    /**
     * 分配督导给咨询师
     */
    User assignSupervisor(Long counselorId, Long supervisorId);

    /**
     * 删除用户
     */
    void deleteUser(Long id);
}