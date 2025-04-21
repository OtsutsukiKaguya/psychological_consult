package com.example.demo.mapper;

import com.example.demo.entity.DiagnosisLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiagnosisLogMapper {

    @Insert("INSERT INTO diagnosis_log (user_id, full_conversation, diagnosis_result, keyword_tag, create_time) " +
            "VALUES (#{userId}, #{fullConversation}, #{diagnosisResult}, #{keywordTag}, #{createTime})")
    void insert(DiagnosisLog log);
}
