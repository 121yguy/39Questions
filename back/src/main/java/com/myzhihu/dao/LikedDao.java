package com.myzhihu.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface LikedDao {

    @Insert("INSERT INTO liked(answer_id, liked_id) VALUES (#{aid}, #{uid})")
    void like(@Param("aid")int aid, @Param("uid")int uid);

    @Delete("DELETE FROM liked WHERE answer_id = #{aid} AND liked_id = #{uid}")
    int cancelLike(@Param("aid")int aid, @Param("uid")int uid);

    @Delete("DELETE FROM liked WHERE answer_id = #{aid}")
    void deleteLikesByAid(@Param("aid")int aid);

    @Select("SELECT liked_id FROM liked WHERE answer_id = #{aid} AND liked_id = #{uid}")
    Integer isLike(@Param("aid")Integer aid, @Param("uid")Integer uid);

    @MapKey("answer_id")
    Map<Integer, Boolean> batchIsLike(@Param("answerIds") List<Integer> answerIds, @Param("userId") int userId);
}
