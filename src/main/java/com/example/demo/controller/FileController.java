package com.example.demo.controller;

import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.FileService;
import com.example.demo.service.OssService;
import com.example.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private OssService ossService;

    // 上传文件
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @ApiParam(value = "要上传的文件", required = true)
            @RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("没有文件上传");
            }
            log.debug("上传文件: {}", file.getOriginalFilename());
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findById(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证");
            }
            // 调用 FileService 保存文件
            File savedFile = fileService.saveFile(file, user.getId());
            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败: " + e.getMessage());
        }
    }

    // 下载文件
    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Integer fileId) {
        try {
            // 根据 fileId 获取文件记录
            File file = fileService.getFile(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();  // 文件未找到
            }

            // 调用 OssService 获取文件内容（文件存储在 OSS 上）
            String fileUrl = file.getOssUrl();
            if (fileUrl == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File URL not found.");
            }

            // 设置下载文件的响应头，浏览器会自动弹出保存文件的对话框
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(ossService.downloadFile(fileUrl));  // 使用 OssService 来返回文件内容

        } catch (Exception e) {
            log.error("Failed to download file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file: " + e.getMessage());
        }
    }

}
