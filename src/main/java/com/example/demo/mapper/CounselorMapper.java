package com.example.demo.mapper;

import com.example.demo.entity.Counselor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CounselorMapper {

    @Select("SELECT * FROM counselor WHERE tag LIKE CONCAT('%', #{tag}, '%')")
    List<Counselor> findByTagLike(@Param("tag") String tag);

    @Select("SELECT id, tag FROM counselor")
    List<Counselor> findAll();

    @Select({
            "<script>",
            "SELECT * FROM counselor WHERE id IN ",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>#{id}</foreach>",
            "</script>"
    })
    List<Counselor> findByIds(List<String> list);
}

