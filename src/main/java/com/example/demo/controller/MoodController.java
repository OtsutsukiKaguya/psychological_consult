package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.MoodDeleteRequest;
import com.example.demo.dto.MoodQueryRequest;
import com.example.demo.entity.Mood;
import com.example.demo.mapper.MoodMapper;
import com.example.demo.service.ExportService;
import com.example.demo.dto.MoodUpsertRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@RestController
public class MoodController {
    @Autowired
    private MoodMapper moodMapper;

    @Autowired
    private ExportService exportService; //注入导出服务

//    @PostMapping("/api/moods")
//    public Result recordMood(@RequestBody Mood mood){
//
//        int i = moodMapper.recordMood(mood);
//        if (i > 0) {
//            Map<String, Object> data = new HashMap<>();
//            data.put("message", "心情记录成功");
//            data.put("status", "recorded");
//            return Result.success(data);
//        } else {
//            return Result.error("心情记录失败");
//        }
//
//    }

    @PostMapping("/api/moods")
    public Result upsertMood(@RequestBody MoodUpsertRequestDTO request) {
        if (request.getUserId() == null || request.getMoodDate() == null) {
            return Result.error("userId 和 moodDate 不能为空");
        }

        Mood existing = moodMapper.selectByUserIdAndDate(request.getUserId(), request.getMoodDate());
        boolean inserted = false;

        if (existing == null) {
            // 插入
            Mood mood = new Mood();
            mood.setUserId(request.getUserId());
            mood.setMoodDate(request.getMoodDate());
            mood.setMoodType(request.getMoodType());
            mood.setMoodContent(request.getMoodContent());

            int result = moodMapper.recordMood(mood);
            if (result <= 0) {
                return Result.error("插入失败");
            }
            inserted = true;
        } else {
            // 更新
            int result = moodMapper.updateMoodContentAndType(
                    request.getUserId(), request.getMoodDate(),
                    request.getMoodType(), request.getMoodContent()
            );
            if (result <= 0) {
                return Result.error("更新失败");
            }
        }

        // 插入或更新后再次查询数据库中的最新记录
        Mood latest = moodMapper.selectByUserIdAndDate(request.getUserId(), request.getMoodDate());
        if (latest == null) {
            return Result.error("操作后未找到记录，数据库可能异常");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("message", inserted ? "记录已新增" : "记录已更新");
        data.put("status", inserted ? "inserted" : "updated");
        data.put("moodType", latest.getMoodType());
        data.put("moodContent", latest.getMoodContent());

        return Result.success(data);
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

    @GetMapping("/api/moods/export")  // 下载心情记录文件
    public ResponseEntity<byte[]> exportMoodRecords(
            @RequestParam("user_id") String userId,
            @RequestParam("format") String format,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate) {

        // 1. 校验格式
        List<String> allowedFormats = Arrays.asList("pdf", "excel", "csv", "txt");
        if (!allowedFormats.contains(format.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(("不支持的格式: " + format).getBytes(StandardCharsets.UTF_8));
        }

        // 2. 查询数据
        List<Mood> records = moodMapper.queryMoodRecords(userId, startDate, endDate, null);
        if (records == null || records.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("导出失败：没有找到符合条件的心情记录".getBytes(StandardCharsets.UTF_8));
        }

        // 3. 生成导出文件字节流
        byte[] fileContent = exportService.generateFile(records, format);

        // 4. 构造响应头
        String filename = "mood_records_" + UUID.randomUUID().toString().substring(0, 6) + "." + format;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(resolveContentType(format));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    private MediaType resolveContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "excel" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "csv", "txt" -> MediaType.TEXT_PLAIN;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }



    @GetMapping("/api/moods/byDate")
    public Result getMoodByDate(@RequestParam("userId") String userId,
                                @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (userId == null || userId.isEmpty() || date == null) {
            return Result.error("userId 和 date 不能为空");
        }

        Mood mood = moodMapper.selectByUserIdAndDate(userId, date);
        if (mood == null) {
            return Result.error("未找到该日期的心情记录");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("moodType", mood.getMoodType());
        data.put("moodContent", mood.getMoodContent());
        data.put("moodDate", mood.getMoodDate());  // 可选返回

        return Result.success(data);
    }



}
