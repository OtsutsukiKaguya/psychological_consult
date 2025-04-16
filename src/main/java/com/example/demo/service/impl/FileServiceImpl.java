//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

//import com.counseling.platform.models.File;
//import com.counseling.platform.repositories.FileRepository;
//import com.counseling.platform.services.FileService;
import com.example.demo.models.File;
import com.example.demo.repositories.FileRepository;
import com.example.demo.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public File saveFile(File file, byte[] data) {
        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString();
            String originalExtension = "";
            if (file.getName() != null && file.getName().contains(".")) {
                originalExtension = file.getName().substring(file.getName().lastIndexOf("."));
            }
            String storedFileName = uniqueFileName + originalExtension;

            // 保存文件到磁盘
            Path filePath = uploadPath.resolve(storedFileName);
            Files.write(filePath, data);

            // 设置文件信息
            file.setStoragePath(storedFileName);
            if (file.getUploadTime() == null) {
                file.setUploadTime(LocalDateTime.now());
            }
            
            // 保存文件记录到数据库
            return fileRepository.save(file);
        } catch (IOException e) {
            log.error("Failed to save file: {}", file.getName(), e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public File getFile(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getFileData(Long id) {
        try {
            File file = fileRepository.findById(id).orElse(null);
            if (file == null || file.getStoragePath() == null) {
                return null;
            }

            Path filePath = Paths.get(uploadDir).resolve(file.getStoragePath());
            if (!Files.exists(filePath)) {
                log.error("File not found on disk: {}", filePath);
                return null;
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read file data for id: {}", id, e);
            throw new RuntimeException("Failed to read file data: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesByUploader(Long uploaderId) {
        return fileRepository.findByUploaderIdOrderByUploadTimeDesc(uploaderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesByName(String name) {
        return fileRepository.findByNameContainingOrderByUploadTimeDesc(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesByType(String contentType) {
        return fileRepository.findByContentTypeContainingOrderByUploadTimeDesc(contentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getRecentFiles(int limit) {
        return fileRepository.findRecentFiles(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        try {
            File file = fileRepository.findById(id).orElse(null);
            if (file == null) {
                log.error("File not found: {}", id);
                throw new IllegalArgumentException("File not found");
            }

            // 删除物理文件
            if (file.getStoragePath() != null) {
                Path filePath = Paths.get(uploadDir).resolve(file.getStoragePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }

            // 删除数据库记录
            fileRepository.deleteById(id);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", id, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public File updateFile(Long id, File updatedFile) {
        File existingFile = fileRepository.findById(id).orElse(null);
        if (existingFile == null) {
            log.error("File not found: {}", id);
            throw new IllegalArgumentException("File not found");
        }

        // 更新文件信息
        if (updatedFile.getName() != null) {
            existingFile.setName(updatedFile.getName());
        }
        
        if (updatedFile.getContentType() != null) {
            existingFile.setContentType(updatedFile.getContentType());
        }
        
        return fileRepository.save(existingFile);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserStorageUsage(Long userId) {
        Long totalSize = fileRepository.getTotalUploadSize(userId);
        return totalSize != null ? totalSize : 0L;
    }
}