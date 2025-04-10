//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.File;
//import com.counseling.platform.models.User;
//import com.counseling.platform.repositories.FileRepository;
import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.repositories.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

//import javax.annotation.PostConstruct;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Autowired
    private FileRepository fileRepository;
    
    private Path fileStoragePath;
    
    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            // 创建文件上传目录（如果不存在）
            if (!Files.exists(fileStoragePath)) {
                Files.createDirectories(fileStoragePath);
                log.info("Created file upload directory: {}", fileStoragePath);
            }
        } catch (IOException ex) {
            log.error("Could not create file upload directory: {}", fileStoragePath);
            throw new RuntimeException("Could not create file upload directory", ex);
        }
    }
    
    /**
     * 保存上传的文件
     */
    public File storeFile(MultipartFile file, User uploader) {
        // 规范化文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 检查文件名
        if (originalFilename.contains("..")) {
            throw new RuntimeException("Invalid file path sequence: " + originalFilename);
        }
        
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        
        try {
            // 保存文件到磁盘
            Path targetLocation = fileStoragePath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file {} to {}", originalFilename, targetLocation);
            
            // 创建文件记录
            File fileEntity = new File();
            fileEntity.setUploader(uploader);
            fileEntity.setFilename(filename);
            fileEntity.setOriginalFilename(originalFilename);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFilePath(targetLocation.toString());
            fileEntity.setUploadTime(LocalDateTime.now());
            
            // 保存文件记录到数据库
            return fileRepository.save(fileEntity);
            
        } catch (IOException ex) {
            log.error("Could not store file {}", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }
    
    /**
     * 获取文件
     */
    public File getFile(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
    
    /**
     * 获取文件路径
     */
    public Path getFilePath(String filename) {
        return fileStoragePath.resolve(filename);
    }
    
    /**
     * 删除文件
     */
    public void deleteFile(Long id) {
        File file = getFile(id);
        if (file != null) {
            try {
                // 从磁盘删除
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
                
                // 从数据库删除
                fileRepository.deleteById(id);
                
                log.info("Deleted file: {}", file.getFilename());
            } catch (IOException ex) {
                log.error("Error deleting file: {}", file.getFilename(), ex);
                throw new RuntimeException("Error deleting file", ex);
            }
        }
    }
}