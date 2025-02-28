package com.myzhihu.dao;

import com.myzhihu.domain.entity.Notice;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

@Mapper
public interface NoticeDao {

    @Insert("INSERT INTO notice(content, recipient_id, operate_time) VALUES (#{content}, #{recipientId}, #{operateTime})")
    void addNotice(Notice notice);

    @Select("SELECT id, content, recipient_id AS recipientId, operate_time AS operateTime, is_read AS isRead FROM notice WHERE recipient_id = #{uid} ORDER BY is_read , operate_time DESC LIMIT #{start}, 6")
    List<Notice> selectNotices(@Param("uid") int uid, @Param("start") int start);

    @Select("SELECT id, content, recipient_id AS recipientId, operate_time AS readTime, is_read AS isRead FROM notice WHERE recipient_id = #{uid} AND notice.is_read = false")
    List<Notice> selectNotReadNotices(int uid);

    @Select("SELECT COUNT(1) FROM notice WHERE recipient_id = #{uid} AND notice.is_read = false")
    int selectNumsOfNotReadNotices(int uid);

    @Update("UPDATE notice SET operate_time = #{time}, is_read = true WHERE id = #{id} AND notice.is_read = false AND recipient_id = #{uid}")
    boolean updateOperateTime(@Param("time") Date time, @Param("id") int id, @Param("uid") int uid);

    @Update("UPDATE notice SET operate_time = #{time}, is_read = true WHERE notice.is_read = false AND recipient_id = #{uid}")
    boolean updateAllOperateTime(@Param("time") Date time, @Param("uid") int uid);
}
