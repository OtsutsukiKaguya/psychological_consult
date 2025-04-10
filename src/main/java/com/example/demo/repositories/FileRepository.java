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

/**
 * 文件存储库接口
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * 根据上传者ID查询文件
     */
    List<File> findByUploaderIdOrderByUploadTimeDesc(Long uploaderId);

    /**
     * 根据文件名查询文件
     */
    List<File> findByNameContainingOrderByUploadTimeDesc(String name);

    /**
     * 根据文件类型查询文件
     */
    List<File> findByContentTypeContainingOrderByUploadTimeDesc(String contentType);

    /**
     * 根据上传时间范围查询文件
     */
    List<File> findByUploadTimeBetweenOrderByUploadTimeDesc(LocalDateTime start, LocalDateTime end);

    /**
     * 根据文件大小范围查询文件
     */
    List<File> findBySizeBetweenOrderByUploadTimeDesc(Long minSize, Long maxSize);

    /**
     * 获取指定用户上传的文件总大小
     */
    @Query("SELECT SUM(f.size) FROM File f WHERE f.uploaderId = :userId")
    Long getTotalUploadSize(@Param("userId") Long userId);

    /**
     * 根据文件扩展名查询文件
     */
    @Query("SELECT f FROM File f WHERE f.name LIKE %:extension")
    List<File> findByExtension(@Param("extension") String extension);

    /**
     * 获取最近上传的文件
     */
    @Query("SELECT f FROM File f ORDER BY f.uploadTime DESC")
    List<File> findRecentFiles(Pageable pageable);

    /**
     * 统计用户上传的文件数量
     */
    @Query("SELECT COUNT(f) FROM File f WHERE f.uploaderId = :userId")
    int countUserFiles(@Param("userId") Long userId);

    /**
     * 删除指定用户的所有文件
     */
    void deleteByUploaderId(Long uploaderId);
}