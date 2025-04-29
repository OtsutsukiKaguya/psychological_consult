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
    User findById(String id);

    /**
     * 根据用户名查找用户
     */
//    User findByUsername(String username);

    /**
     * 检查用户名是否已存在
     */
    boolean existsById(String id);

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
    User updateUser(String id, User user);

    /**
     * 更新用户状态
     */
    User updateUserStatus(String id, User.UserStatus status);

//    /**
//     * 根据用户名更新用户状态
//     */
//    User updateUserStatusByUsername(String username, String status);

    /**
     * 更新用户头像
     */
    User updateUserAvatar(String id, String avatarUrl);

//    /**
//     * 获取咨询师的督导
//     */
//    User getSupervisor(String counselorId);

//    /**
//     * 获取督导的咨询师
//     */
//    List<User> getSupervisees(String supervisorId);

//    /**
//     * 分配督导给咨询师
//     */
//    User assignSupervisor(String counselorId, String supervisorId);

    /**
     * 删除用户
     */
    void deleteUser(String id);
}