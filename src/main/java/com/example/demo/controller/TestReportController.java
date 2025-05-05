// src/main/java/com/example/demo/controller/TestReportController.java
package com.example.demo.controller;

import com.example.demo.entity.TestReport;
import com.example.demo.mapper.TestReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class TestReportController {

    @Autowired
    private TestReportMapper reportMapper;

    /** 获取所有记录 **/
    @GetMapping
    public List<TestReport> getAll() {
        return reportMapper.findAll();
    }

    /** 插入一条新记录（testId 由数据库自增生成） **/
    @PostMapping
    public ResponseEntity<TestReport> create(@RequestBody TestReport report) {
        // 自动设置当前日期
        report.setDate(LocalDate.now().toString());

        int rows = reportMapper.insert(report);
        if (rows > 0) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

}
