package com.example.demo.mapper;

import com.example.demo.entity.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NotificationMapper {

    // 插入通知内容
    @Insert("INSERT INTO notification(notification_id, notification_time, notification_title, sender_id, notification_content, notification_isdeleted, picture_link) " +
            "VALUES (#{notificationId}, #{notificationTime}, #{notificationTitle}, #{senderId}, #{notificationContent}, #{notificationIsDeleted}, #{pictureLink})")
    int insertNotification(Notification notification);

    // 插入通知接收者（群发核心）
    @Insert("INSERT INTO notification_receiver(notification_id, receiver_id, is_read) VALUES(#{notificationId}, #{receiverId}, #{isRead})")
    int insertNotificationReceiver(@Param("notificationId") String notificationId,
                                   @Param("receiverId") String receiverId,
                                   @Param("isRead") boolean isRead);

    // 获取某接收者收到的所有通知（通过关联表）
    @Select("SELECT n.* FROM notification n " +
            "JOIN notification_receiver r ON n.notification_id = r.notification_id " +
            "WHERE r.receiver_id = #{receiverId} AND n.notification_isdeleted = false " +
            "ORDER BY n.notification_time DESC")
    List<Notification> getNotificationsByReceiverId(@Param("receiverId") String receiverId);

    // 获取某发送者发出的通知
    @Select("SELECT * FROM notification WHERE sender_id = #{senderId} AND notification_isdeleted = false ORDER BY notification_time DESC")
    List<Notification> getNotificationsBySenderId(@Param("senderId") String senderId);

    // 获取通知详情
    @Select("SELECT * FROM notification WHERE notification_id = #{id}")
    Notification getNotificationById(@Param("id") String id);

    // 逻辑删除通知
    @Update("UPDATE notification SET notification_isdeleted = 1 WHERE notification_id = #{id}")
    int deleteNotificationById(@Param("id") String id);

    // 标记通知为已读
    @Update("UPDATE notification_receiver SET is_read = true WHERE notification_id = #{notificationId} AND receiver_id = #{receiverId}")
    int markNotificationAsRead(@Param("notificationId") String notificationId,
                               @Param("receiverId") String receiverId);

}
