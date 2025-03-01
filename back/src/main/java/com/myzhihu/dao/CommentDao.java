package com.myzhihu.dao;

import com.myzhihu.domain.dto.CommentWithUserInfo;
import com.myzhihu.domain.entity.Comment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {

    @Select("SELECT comments.id, content, author_id AS authorId, time, icon, nickName FROM comments JOIN user_info u on u.userId = comments.author_id WHERE comments.id < #{startId} ORDER BY id DESC LIMIT 12")
    List<CommentWithUserInfo> getCommentsByStartId(long startId);

    @Select("SELECT id FROM comments WHERE author_id = #{uid} AND id = #{id}")
    Integer getCommentByUidAndId(@Param("uid") int uid, @Param("id") int id);

    @Insert("INSERT comments(content, author_id, time) VALUE (#{content}, #{authorId}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addComment(Comment comment);

    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteCommentById(int id);

    @Delete("DELETE FROM comments WHERE id = #{id} AND author_id = #{uid}")
    int deleteCommentByIdAndUid(@Param("id") int id, @Param("uid") int uid);

}
