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

    @Select("SELECT test_id AS testId, test_name AS testName, content, date, user_id AS userId FROM test_report WHERE user_id = #{userId}")
    List<TestReport> findByUserId(@Param("userId") String userId);

    @Select("SELECT test_id AS testId, test_name AS testName, content, date, user_id AS userId FROM test_report WHERE user_id = #{userId} AND test_name = #{testName}")
    List<TestReport> findByUserIdAndTestName(@Param("userId") String userId, @Param("testName") String testName);

}
