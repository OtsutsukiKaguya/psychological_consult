package com.example.demo.controller;

import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.FileService;
import com.example.demo.service.OssService;
import com.example.demo.service.UserService;
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
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.debug("Uploading file: {}", file.getOriginalFilename());

            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findById(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 调用 FileService 保存文件
            File savedFile = fileService.saveFile(file, user.getId());

            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
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

            // 调用 OssService 下载文件
            String localFilePath = "/path/to/save/location/" + file.getOriginalName();  // 指定保存文件的本地路径
            java.io.File downloadedFile = ossService.downloadFile(file.getOssUrl(), localFilePath);

            if (downloadedFile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file.");
            }

            // 成功下载文件并返回
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(Files.readAllBytes(downloadedFile.toPath()));  // 将文件内容作为响应体返回

        } catch (IOException e) {
            log.error("Failed to download file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file: " + e.getMessage());
        }
    }
}
