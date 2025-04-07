package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.MoodDeleteRequest;
import com.example.demo.dto.MoodQueryRequest;
import com.example.demo.entity.Mood;
import com.example.demo.mapper.MoodMapper;
import com.example.demo.service.ExportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MoodController {
    @Autowired
    private MoodMapper moodMapper;

    @Autowired
    private ExportService exportService; //注入导出服务

    @PostMapping("/api/moods")
    public Result recordMood(@RequestBody Mood mood){

        int i = moodMapper.recordMood(mood);
        if (i > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "心情记录成功");
            data.put("status", "recorded");
            return Result.success(data);
        } else {
            return Result.error("心情记录失败");
        }

    }

    @PostMapping("/api/moods/query")
    public Result queryMoodRecords(@RequestBody MoodQueryRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("user_id 不能为空");
        }

        List<Mood> records = moodMapper.queryMoodRecords(
                request.getUserId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMoodType()
        );

        return Result.success(records);
    }

    @DeleteMapping("/api/moods")
    public Result deleteMoodRecord(@RequestBody MoodDeleteRequest request) {
        if (request.getUserId() == null || request.getMoodDate() == null) {
            throw new IllegalArgumentException("userId 和 date 不能为空");
        }

        int deleted = moodMapper.deleteByUserIdAndDate(request.getUserId(), request.getMoodDate());
        if (deleted > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("未找到符合条件的心情记录");
        }
    }

    @GetMapping("/api/moods/export")  //以不同文件格式导出
    public Result exportMoodRecords(@RequestParam("user_id") String userId,
                                    @RequestParam("format") String format,
                                    @RequestParam(value = "start_date", required = false) String startDate,
                                    @RequestParam(value = "end_date", required = false) String endDate,
                                    HttpServletRequest request) {
        // 1. 校验导出格式是否支持
        List<String> allowedFormats = Arrays.asList("pdf", "excel", "csv", "txt");
        if (!allowedFormats.contains(format.toLowerCase())) {
            return Result.error(422, "format 不在允许范围内（pdf / excel / csv / txt）");
        }

        // 2. 查询符合条件的心情记录
        List<Mood> records = moodMapper.queryMoodRecords(userId, startDate, endDate, null);
        if (records == null || records.isEmpty()) {
            return Result.error("导出失败：没有找到符合条件的心情记录");
        }

        // 3. 调用 ExportService 生成文件
        String filename = "mood_records_" + UUID.randomUUID().toString().substring(0, 6) + "." + format;
        String filePath = exportService.generateFile(records, format, filename);

        // 4. 构造导出链接
        String downloadUrl = request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                "/exports/" + filename;

        Map<String, Object> data = new HashMap<>();
        data.put("message", "导出成功");
        data.put("export_url", downloadUrl);
        data.put("format", format);
        data.put("file_name", filename);
        data.put("record_count", records.size());

        return Result.success(data);
    }


}
