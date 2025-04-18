package com.example.demo.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class OssService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    // 上传文件
    public String uploadFile(MultipartFile file) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

            // 创建临时文件
            File tempFile = convertMultipartFileToFile(file);

            // 上传文件到 OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, tempFile);
            ossClient.putObject(putObjectRequest);

            // 删除临时文件
            tempFile.delete();

            // 返回文件访问 URL
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 下载文件
    public File downloadFile(String filename, String localFilePath) {
        try {
            // 获取 OSS 对象
            OSSObject ossObject = ossClient.getObject(bucketName, filename);

            // 获取文件输入流
            InputStream inputStream = ossObject.getObjectContent();

            // 创建输出文件
            File localFile = new File(localFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            // 将文件从 OSS 写入本地文件
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            // 关闭流
            fileOutputStream.close();
            inputStream.close();

            return localFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将 MultipartFile 转换为 File
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    // 删除文件
    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        ossClient.deleteObject(bucketName, fileName);
    }

    //基于ossUrl下载文件
    public byte[] downloadFile(String ossUrl) {
        try {
            // 从完整 URL 中提取 OSS 内部的文件名（key）
            String fileName = ossUrl.substring(ossUrl.lastIndexOf("/") + 1);

            // 获取 OSS 对象（文件内容）
            OSSObject ossObject = ossClient.getObject(bucketName, fileName);
            InputStream inputStream = ossObject.getObjectContent();

            // 读取文件内容为字节数组（Java 8 兼容写法）
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
