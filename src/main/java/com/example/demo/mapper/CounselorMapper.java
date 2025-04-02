package com.example.demo.mapper;

import com.example.demo.entity.Counselor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CounselorMapper {

    @Select("SELECT * FROM counselor WHERE tag LIKE CONCAT('%', #{tag}, '%')")
    List<Counselor> findByTagLike(String tag);
}

