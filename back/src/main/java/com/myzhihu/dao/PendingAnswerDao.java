package com.myzhihu.dao;

import com.myzhihu.domain.dto.PendingAnswerWithQuestionTitle;
import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.PendingAnswer;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PendingAnswerDao {

    @Select("SELECT id, text, author_id AS authorId, question_id AS questionId FROM pending_answer WHERE reviewed = false LIMIT #{start}, 20")
    List<Answer> getAnswers(@Param("start") int start);

    @Insert("INSERT INTO pending_answer(text, author_id, question_id, reviewed, related_answer) VALUES (#{text}, #{authorId}, #{questionId}, false, #{relatedAnswer})")
    int addPendingAnswerAndSubmit(PendingAnswer pendingAnswer);

    @Insert("INSERT INTO pending_answer(text, author_id, question_id, reviewed, related_answer) VALUES (#{text}, #{authorId}, #{questionId}, true, #{relatedAnswer})")
    int addPendingAnswer(PendingAnswer pendingAnswer);

    @Update("UPDATE pending_answer SET text = #{text} WHERE id = #{id}")
    int updateTextById(@Param("text") String text, @Param("id") Integer id);

    @Update("UPDATE pending_answer SET text = #{text} WHERE id = #{id} AND author_id = #{uid}")
    int updateTextByIdAndUid(@Param("text") String text, @Param("id") Integer id, @Param("uid") Integer uid);

    @Update("UPDATE pending_answer SET reviewed = true WHERE id = #{id}")
    int letAnswerReviewed(@Param("id") Integer id);

    @Update("UPDATE pending_answer SET reviewed = false WHERE id = #{id}")
    void letAnswerUnreviewed(@Param("id") Integer id);

    @Delete("DELETE FROM pending_answer WHERE id = #{id}")
    void deleteById(@Param("id") Integer id);

    @Delete("DELETE FROM pending_answer WHERE id = #{id} AND author_id = #{uid}")
    int deleteByIdAndUid(@Param("id") Integer id, @Param("uid") Integer uid);

    @Select("SELECT id, text, author_id AS authorId, question_id AS questionId FROM pending_answer WHERE id = #{id}")
    Answer selectAsAnswerById(@Param("id") Integer id);

    @Select("SELECT id, text, author_id AS authorId, question_id AS questionId, related_answer AS relatedAnswer FROM pending_answer WHERE id = #{id}")
    PendingAnswer selectAsPendingAnswerById(@Param("id") Integer id);

    @Select("SELECT pending_answer.id, pending_answer.text, author_id AS authorId, question_id AS questionId, reviewed, title FROM pending_answer JOIN question q on q.id = pending_answer.question_id WHERE author_id = #{uid} LIMIT #{start}, 12")
    List<PendingAnswerWithQuestionTitle> selectByUid(@Param("uid") int uid, @Param("start") int start);

    @Select("SELECT id FROM pending_answer WHERE id = #{id} AND author_id = #{uid}")
    Integer selectByIdAndUid(@Param("id") int id, @Param("uid") int uid);

    @Select("SELECT id, text, author_id AS authorId, question_id AS questionId FROM pending_answer WHERE id = #{id} AND author_id = #{uid}")
    Answer getByIdAndUid(@Param("id") int id, @Param("uid") int uid);
}
