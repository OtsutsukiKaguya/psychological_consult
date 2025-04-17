//package com.counseling.platform.controllers;
package com.example.demo.controller;

//import com.counseling.platform.models.User;
//import com.counseling.platform.services.UserService;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 处理用户相关的请求
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     * 仅管理员可访问
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all users: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            
            // 如果不是管理员，且不是查询自己的信息，则拒绝
            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this user's information");
            }
            
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to get user by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            
            // 如果不是管理员，且不是修改自己的信息，则拒绝
            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this user's information");
            }
            
            // 如果是普通用户，不允许修改角色
            if (currentUser.getRole() != User.UserRole.ADMIN && request.getRole() != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to change role");
            }
            
            // 构建更新对象
            User userToUpdate = User.builder()
                    .id(request.getUsername())
                    .password(request.getPassword())
                    .name(request.getNickname())
                    .build();
            
            // 仅管理员可以修改角色
            if (currentUser.getRole() == User.UserRole.ADMIN && request.getRole() != null) {
                userToUpdate.setRole(User.UserRole.valueOf(request.getRole()));
            }
            
            // 执行更新
            User updatedUser = userService.updateUser(id, userToUpdate);
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request to update user: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            
            // 如果不是管理员，且不是更新自己的状态，则拒绝
            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this user's status");
            }
            
            // 执行更新
            User updatedUser = userService.updateUserStatus(id, User.UserStatus.valueOf(request.getStatus()));
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request to update user status: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update user status: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user status: " + e.getMessage());
        }
    }

    /**
     * 更新用户头像
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateUserAvatar(@PathVariable String id, @Valid @RequestBody UpdateAvatarRequest request) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            
            // 如果不是管理员，且不是更新自己的头像，则拒绝
            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this user's avatar");
            }
            
            // 执行更新
            User updatedUser = userService.updateUserAvatar(id, request.getAvatarUrl());
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user avatar: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user avatar: " + e.getMessage());
        }
    }

//    /**
//     * 分配督导给咨询师
//     * 仅管理员可访问
//     */
//    @PutMapping("/{counselorId}/supervisor/{supervisorId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> assignSupervisor(@PathVariable String counselorId, @PathVariable String supervisorId) {
//        try {
//            User updatedUser = userService.assignSupervisor(counselorId, supervisorId);
//            if (updatedUser == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid request to assign supervisor", e);
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            log.error("Failed to assign supervisor", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign supervisor: " + e.getMessage());
//        }
//    }

//    /**
//     * 获取督导的咨询师
//     */
//    @GetMapping("/supervisor/{supervisorId}/supervisees")
//    public ResponseEntity<?> getSupervisees(@PathVariable String supervisorId) {
//        try {
//            // 获取当前用户
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = userService.findById(authentication.getName());
//
//            // 如果不是管理员，且不是查询自己的下属，则拒绝
//            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(supervisorId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this information");
//            }
//
//            List<User> supervisees = userService.getSupervisees(supervisorId);
//            return ResponseEntity.ok(supervisees);
//        } catch (Exception e) {
//            log.error("Failed to get supervisees", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get supervisees: " + e.getMessage());
//        }
//    }

//    /**
//     * 获取咨询师的督导
//     */
//    @GetMapping("/counselor/{counselorId}/supervisor")
//    public ResponseEntity<?> getSupervisor(@PathVariable String counselorId) {
//        try {
//            // 获取当前用户
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = userService.findById(authentication.getName());
//
//            // 如果不是管理员，且不是查询自己的督导，则拒绝
//            if (currentUser.getRole() != User.UserRole.ADMIN && !currentUser.getId().equals(counselorId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this information");
//            }
//
//            User supervisor = userService.getSupervisor(counselorId);
//            if (supervisor == null) {
//                return ResponseEntity.noContent().build();
//            }
//
//            return ResponseEntity.ok(supervisor);
//        } catch (Exception e) {
//            log.error("Failed to get supervisor", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get supervisor: " + e.getMessage());
//        }
//    }

    /**
     * 删除用户
     * 仅管理员可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request to delete user: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }

    /**
     * 更新用户请求
     */
    @Data
    public static class UpdateUserRequest {
        private String username;
        private String password;
        private String nickname;
        private String role;
    }

    /**
     * 更新状态请求
     */
    @Data
    public static class UpdateStatusRequest {
        private String status;
    }

    /**
     * 更新头像请求
     */
    @Data
    public static class UpdateAvatarRequest {
        private String avatarUrl;
    }
}