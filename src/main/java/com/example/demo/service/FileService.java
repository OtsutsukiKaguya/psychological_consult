//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.File;
import com.example.demo.models.File;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 保存文件
     */
    File saveFile(File file, byte[] data);

    /**
     * 根据ID获取文件
     */
    File getFile(Long id);

    /**
     * 获取文件数据
     */
    byte[] getFileData(Long id);

    /**
     * 根据上传者ID获取文件
     */
    List<File> getFilesByUploader(Long uploaderId);

    /**
     * 根据文件名获取文件
     */
    List<File> getFilesByName(String name);

    /**
     * 根据文件类型获取文件
     */
    List<File> getFilesByType(String contentType);

    /**
     * 获取最近上传的文件
     */
    List<File> getRecentFiles(int limit);

    /**
     * 删除文件
     */
    void deleteFile(Long id);

    /**
     * 更新文件信息
     */
    File updateFile(Long id, File file);

    /**
     * 获取用户的存储使用情况
     */
    long getUserStorageUsage(Long userId);
}