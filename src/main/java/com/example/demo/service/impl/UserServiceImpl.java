//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

//import com.counseling.platform.models.User;
//import com.counseling.platform.repositories.UserRepository;
//import com.counseling.platform.services.UserService;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    //感觉不需要专门写一个根据name查找，而可以直接用后面的模糊匹配
//    @Override
//    @Transactional(readOnly = true)
//    public User findByUsername(String username) {
//        return userRepository.findById(username).orElse(null);
//    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByStatus(User.UserStatus status) {
        return userRepository.findByState(status);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsById(user.getId())) {
            log.error("Username already exists: {}", user.getId());
            throw new IllegalArgumentException("Username already exists");
        }
        
        // 如果没有设置创建时间，则设置为当前时间
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        // 如果密码没有加密，则加密密码
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(String id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        
        // 更新用户信息
        if (updatedUser.getId() != null && !updatedUser.getId().equals(existingUser.getId())) {
            // 检查新用户名是否已存在
            if (userRepository.existsById(updatedUser.getId())) {
                log.error("Username already exists: {}", updatedUser.getId());
                throw new IllegalArgumentException("Username already exists");
            }
            existingUser.setId(updatedUser.getId());
        }
        
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            // 加密密码
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }
        
        if (updatedUser.getState() != null) {
            existingUser.setState(updatedUser.getState());
        }
        
        if (updatedUser.getIdPictureLink() != null) {
            existingUser.setIdPictureLink(updatedUser.getIdPictureLink());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User updateUserStatus(String id, User.UserStatus status) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        
        user.setState(status);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    //感觉不需要根据用户名来更新状态，所以注释掉
//    @Override
//    @Transactional
//    public User updateUserStatusByUsername(String username, String status) {
//        User user = userRepository.findByUsername(username);
//        if (user == null) {
//            return null;
//        }
//
//        try {
//            User.UserStatus userStatus = User.UserStatus.valueOf(status);
//            user.setStatus(userStatus);
//            user.setUpdatedAt(LocalDateTime.now());
//
//            return userRepository.save(user);
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid user status: {}", status);
//            throw new IllegalArgumentException("Invalid user status");
//        }
//    }

    @Override
    @Transactional
    public User updateUserAvatar(String id, String avatarUrl) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        
        user.setIdPictureLink(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

//    //获取咨询师对应的督导
//    @Override
//    @Transactional(readOnly = true)
//    public User getSupervisor(String counselorId) {
//        User counselor = userRepository.findById(counselorId).orElse(null);
//        if (counselor == null) {
//            return null;
//        }
//
//        return counselor.getSupervisor();
//    }

//    //获取咨询师
//    @Override
//    @Transactional(readOnly = true)
//    public List<User> getSupervisees(String supervisorId) {
//        return userRepository.findBySupervisorId(supervisorId);
//    }

//    //设置督导关系
//    @Override
//    @Transactional
//    public User assignSupervisor(String counselorId, String supervisorId) {
//        User counselor = userRepository.findById(counselorId).orElse(null);
//        User supervisor = userRepository.findById(supervisorId).orElse(null);
//
//        if (counselor == null || supervisor == null) {
//            return null;
//        }
//
//        // 检查督导角色
//        if (supervisor.getRole() != User.UserRole.SUPERVISOR && supervisor.getRole() != User.UserRole.ADMIN) {
//            log.error("User is not a supervisor or admin: {}", supervisorId);
//            throw new IllegalArgumentException("User is not a supervisor or admin");
//        }
//
//        // 设置督导关系
//        counselor.setSupervisor(supervisor);
//        counselor.setUpdatedAt(LocalDateTime.now());
//
//        return userRepository.save(counselor);
//    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            log.error("User not found: {}", id);
            throw new IllegalArgumentException("User not found");
        }
        
        userRepository.deleteById(id);
    }
}