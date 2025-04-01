package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class testDBController {
    @Autowired
    private DataSource dataSource;

    @GetMapping("/ping-db")
    public String ping() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return "连接成功：" + conn.getMetaData().getURL();
        }
    }
}
