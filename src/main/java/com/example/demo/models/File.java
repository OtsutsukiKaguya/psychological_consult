package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// 文件实体类
@Entity
@Table(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 修改 id 为 Integer 类型，以匹配数据库中的 INT 类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;  // uploader_id 引用 User 表中的 id 字段

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;  // 修改 fileSize 为 Integer 类型，以匹配数据库中的 INT 类型

    @Column(name = "file_url", nullable = false)
    private String ossUrl;  // 修改字段名为 file_url，并保留 String 类型

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    // 引用此文件的消息
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ChatMessage> messages = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
    }
}
