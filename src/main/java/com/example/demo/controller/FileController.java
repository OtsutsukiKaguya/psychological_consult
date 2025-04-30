package com.example.demo.controller;

import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.FileService;
import com.example.demo.service.OssService;
import com.example.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletResponse;
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
import java.io.InputStream;
import java.io.OutputStream;
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

    @GetMapping("/{fileId}/download")
    public void downloadFile(@PathVariable Integer fileId, HttpServletResponse response) {
        try {
            // 1. 根据 fileId 获取文件记录
            File file = fileService.getFile(fileId);
            if (file == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
                return;
            }

            // 2. 获取文件 URL
            String fileUrl = file.getOssUrl();
            if (fileUrl == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("File URL not found.");
                return;
            }

            // 3. 通过 ossService 下载文件内容（InputStream）
            InputStream inputStream = ossService.downloadFile(fileUrl);
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to download file from OSS.");
                return;
            }

            // 4. 设置响应头
            response.setContentType(file.getFileType() != null ? file.getFileType() : "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"");

            // 5. 将文件内容写入到 response 的输出流（流式处理）
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();
        } catch (Exception e) {
            log.error("Failed to download file", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to download file: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("Failed to write error response", ioException);
            }
        }
    }


}
