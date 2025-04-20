package com.example.demo.service.impl;

import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.repositories.FileRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.OssService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private OssService ossService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public File saveFile(MultipartFile file, String uploaderId) {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            // 获取上传者信息
            User uploader = userService.findById(uploaderId);
            if (uploader == null) {
                throw new RuntimeException("Uploader not found");
            }

            // 上传文件到 OSS 并获取文件 URL
            String ossUrl = ossService.uploadFile(file);
            if (ossUrl == null) {
                throw new RuntimeException("Failed to upload file to OSS");
            }

            // 保存文件记录到数据库
            File fileEntity = new File();
            fileEntity.setUploader(uploader);
            fileEntity.setOriginalName(originalFilename);
            fileEntity.setFileSize((int) file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setOssUrl(ossUrl);
            fileEntity.setUploadTime(LocalDateTime.now());

            return fileRepository.save(fileEntity);
        } catch (Exception ex) {
            log.error("Failed to save file: {}", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public File getFile(Integer id) {
        return fileRepository.findById(id).orElse(null);  // 根据 ID 查询文件
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesByUploader(String uploaderId) {
        return fileRepository.findByUploaderIdOrderByUploadTimeDesc(uploaderId);  // 获取指定用户上传的文件
    }

    @Override
    @Transactional(readOnly = true)
    public File getFileByUrl(String ossUrl) {
        return fileRepository.findByOssUrl(ossUrl);  // 调用 Repository 查找文件
    }

    @Override
    @Transactional
    public void deleteFile(Integer id) {
        try {
            File file = getFile(id);
            if (file != null) {
                // 从 OSS 删除文件
                ossService.deleteFile(file.getOssUrl());

                // 从数据库删除文件记录
                fileRepository.deleteById(id);
                log.info("Deleted file: {}", file.getOriginalName());
            }
        } catch (Exception e) {
            log.error("Error deleting file with id: {}", id, e);
            throw new RuntimeException("Error deleting file", e);
        }
    }

    //基于ossUrl下载文件
    @Override
    public byte[] downloadFile(String ossUrl) {
        return ossService.downloadFile(ossUrl);
    }
}

