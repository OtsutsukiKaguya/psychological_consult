package com.example.demo.mapper;

import com.example.demo.entity.TestReport;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestReportMapper {

    @Select("SELECT test_id AS testId, test_name AS testName, content, date, user_id AS userId FROM test_report")
    List<TestReport> findAll();

    @Insert("INSERT INTO test_report(test_name, content, date, user_id) " +
            "VALUES(#{testName}, #{content}, #{date}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "testId", keyColumn = "test_id")
    int insert(TestReport report);
}
