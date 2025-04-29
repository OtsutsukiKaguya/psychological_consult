//package com.counseling.platform.repositories;
package com.example.demo.repositories;

//import com.counseling.platform.models.File;
import com.example.demo.models.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {  // 修改主键类型为 Integer

    // 根据上传者ID查询文件
    List<File> findByUploaderIdOrderByUploadTimeDesc(String uploaderId);  // 修改上传者ID为 String 类型

    // 根据文件 URL 查找文件
    File findByOssUrl(String ossUrl);

    // 根据文件名查询文件
    List<File> findByOriginalNameContainingOrderByUploadTimeDesc(String originalName);  // 修改字段名为 originalName

    // 根据文件类型查询文件
    List<File> findByFileTypeContainingOrderByUploadTimeDesc(String fileType);  // 修改字段名为 fileType

    // 根据上传时间范围查询文件
    List<File> findByUploadTimeBetweenOrderByUploadTimeDesc(LocalDateTime start, LocalDateTime end);  // 无需修改

    // 根据文件大小范围查询文件
    List<File> findByFileSizeBetweenOrderByUploadTimeDesc(Integer minSize, Integer maxSize);  // 修改字段名为 fileSize，并将类型改为 Integer

    // 获取指定用户上传的文件总大小
    @Query("SELECT SUM(f.fileSize) FROM File f WHERE f.uploader.id = :userId")  // 修改字段名为 fileSize，Uploader 的字段改为 id
    int getTotalUploadSize(@Param("userId") String userId);  // 修改上传者ID为 String 类型

    // 根据文件扩展名查询文件
    @Query("SELECT f FROM File f WHERE f.originalName LIKE %:extension")  // 修改字段名为 originalName
    List<File> findByExtension(@Param("extension") String extension);

    // 获取最近上传的文件
    @Query("SELECT f FROM File f ORDER BY f.uploadTime DESC")
    List<File> findRecentFiles(Pageable pageable);

    // 统计用户上传的文件数量
    @Query("SELECT COUNT(f) FROM File f WHERE f.uploader.id = :userId")  // 修改字段名为 uploader.id
    int countUserFiles(@Param("userId") String userId);  // 修改上传者ID为 String 类型

    // 删除指定用户的所有文件
    void deleteByUploaderId(String uploaderId);  // 修改上传者ID为 String 类型
}
