package com.myzhihu.dao;

import com.myzhihu.domain.entity.PendingQuestion;
import com.myzhihu.domain.entity.Question;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PendingQuestionDao {

    @Select("SELECT id, title, text, author_id AS authorId FROM pending_question WHERE reviewed = false LIMIT #{start}, 20")
    List<Question> getQuestions(@Param("start") int start);

    @Insert("INSERT INTO pending_question(title, text, author_id, reviewed) VALUES (#{title}, #{text}, #{authorId}, false)")
    boolean addPendingQuestionAndSubmit(Question question);

    @Insert("INSERT INTO pending_question(title, text, author_id, reviewed) VALUES (#{title}, #{text}, #{authorId}, true)")
    boolean addPendingQuestion(Question question);

    @Update("UPDATE pending_question SET title = #{title}, text = #{text} WHERE id = #{id}")
    void updateQuestionById(@Param("title") String title, @Param("text") String text, @Param("id") Integer id);

    @Update("UPDATE pending_question SET title = #{title}, text = #{text} WHERE id = #{id} AND author_id = #{uid}")
    int updateQuestionByIdAndUid(@Param("title") String title, @Param("text") String text, @Param("id") Integer id, @Param("uid") Integer uid);

    @Update("UPDATE pending_question SET reviewed = true WHERE id = #{id}")
    int letQuestionReviewed(@Param("id") Integer id);

    @Update("UPDATE pending_question SET reviewed = false WHERE id = #{id}")
    void letQuestionUnreviewed(@Param("id") Integer id);

    @Delete("DELETE FROM pending_question WHERE id = #{id}")
    void deleteById(@Param("id") Integer id);

    @Delete("DELETE FROM pending_question WHERE id = #{id} AND author_id = #{uid}")
    int deleteByIdAndUid(@Param("id") Integer id, @Param("uid") Integer uid);

    @Select("SELECT id, title, text, author_id AS authorId FROM pending_question WHERE id = #{id}")
    Question selectById(@Param("id") Integer id);

    @Select("SELECT id, title, text, author_id AS authorId, reviewed FROM pending_question WHERE author_id = #{uid} LIMIT #{start}, 12")
    List<PendingQuestion> selectByUid(@Param("uid") int uid, @Param("start") int start);

    @Select("SELECT id FROM pending_question WHERE author_id = #{uid} AND id = #{id}")
    Integer selectByIdAndUid(@Param("id") int id, @Param("uid") int uid);
}
