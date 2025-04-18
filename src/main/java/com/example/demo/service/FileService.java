package com.example.demo.service;

import com.example.demo.models.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 保存文件
     */
    File saveFile(MultipartFile file, String uploaderId);  // 修改为 MultipartFile 类型和上传者ID

    /**
     * 根据ID获取文件
     */
    File getFile(Integer id);  // 修改为 Integer 类型以匹配数据库定义

    /**
     * 根据上传者ID获取文件
     */
    List<File> getFilesByUploader(String uploaderId);  // 修改为 String 类型

    // 根据文件 URL 查找文件
    File getFileByUrl(String ossUrl);

    /**
     * 删除文件
     */
    void deleteFile(Integer id);  // 修改为 Integer 类型

    //基于ossUrl下载文件
    byte[] downloadFile(String ossUrl);

}
