package com.example.demo.mapper;

import com.example.demo.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 获取接收者收到的通知
    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} ORDER BY notification_time DESC")
    List<Notification> getNotificationsByReceiverId(@Param("receiverId") String receiverId);

    // 获取发送者发送的通知
    @Select("SELECT * FROM notification WHERE sender_id = #{senderId} ORDER BY notification_time DESC")
    List<Notification> getNotificationsBySenderId(@Param("senderId") String senderId);

    // 获取某条通知详情
    @Select("SELECT * FROM notification WHERE notification_id = #{id}")
    Notification getNotificationById(@Param("id") String id);

    // 插入通知
    @Insert("INSERT INTO notification(notification_id, notification_time, notification_title, sender_id, receiver_id, notification_content, notification_isdeleted, picture_link) " +
            "VALUES (#{notificationId}, #{notificationTime}, #{notificationTitle}, #{senderId}, #{receiverId}, #{notificationContent}, #{notificationIsDeleted}, #{pictureLink})")
    int insertNotification(Notification notification);

    // 逻辑删除通知（设置为已删除）
    @Update("UPDATE notification SET notification_isdeleted = 1 WHERE notification_id = #{id}")
    int deleteNotificationById(@Param("id") String id);

    // 获取接收者未删除通知数量
    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id = #{receiverId} AND notification_isdeleted = 0")
    int countNotDeletedByReceiverId(@Param("receiverId") String receiverId);
}
