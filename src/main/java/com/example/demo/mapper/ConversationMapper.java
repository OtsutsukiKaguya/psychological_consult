package com.example.demo.mapper;

import com.example.demo.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConversationMapper {

    @Select("SELECT * FROM conversation")
    public List<Conversation> find();

    @Select("SELECT * FROM Conversation WHERE user_id=#{id} OR counselor_id=#{id}")
    public List<Conversation> findBySingleId(String id);

    @Select("SELECT * FROM Conversation WHERE date=#{date}")
    public List<Conversation> findByDate(String date);

    @Select("SELECT * FROM Conversation WHERE (user_id=#{id} OR counselor_id=#{id}) AND date=#{date}")
    public List<Conversation> findByIdAndDate(String id, String date);

    @Select("SELECT * FROM Conversation WHERE (user_id=#{userid} AND counselor_id=#{counselorid})")
    public List<Conversation> findByUseridAndCounselorid(String userid, String counselorid);
}
