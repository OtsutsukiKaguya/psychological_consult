//package com.counseling.platform.controllers;
package com.example.demo.controller;

import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
//import com.counseling.platform.models.File;
//import com.counseling.platform.models.User;
//import com.counseling.platform.services.FileService;
//import com.counseling.platform.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserService userService;
    
    @Value("${file.upload.directory}")
    private String uploadDir;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.debug("Upload file: {}", file.getOriginalFilename());
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件到磁盘
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 保存文件记录到数据库
            File fileEntity = File.builder()
                    .uploader(user)
                    .originalName(originalFilename)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .ossUrl(newFilename)
                    .build();
            
            File savedFile = fileService.save(fileEntity);
            
            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFileInfo(@PathVariable Long fileId) {
        File file = fileService.findById(fileId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(file);
    }

    /**
     * 下载文件
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        try {
            File file = fileService.findById(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(uploadDir).resolve(file.getOssUrl());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            log.error("Failed to download file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file: " + e.getMessage());
        }
    }

    /**
     * 获取用户上传的文件
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFiles(@PathVariable Long userId) {
        // 检查权限（仅允许查看自己的文件或管理员权限）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to view these files");
        }
        
        List<File> files = fileService.findByUploaderId(userId);
        return ResponseEntity.ok(files);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            // 获取文件信息
            File file = fileService.findById(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 检查权限（仅允许删除自己的文件或管理员权限）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            if (!file.getUploader().getId().equals(currentUser.getId()) && currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this file");
            }
            
            // 删除文件记录
            fileService.delete(fileId);
            
            // 删除磁盘上的文件
            Path filePath = Paths.get(uploadDir).resolve(file.getOssUrl());
            Files.deleteIfExists(filePath);
            
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Failed to delete file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file: " + e.getMessage());
        }
    }
}