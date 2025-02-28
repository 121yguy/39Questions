package com.myzhihu.dao;

import com.myzhihu.domain.dto.AnswerWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.PendingAnswer;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnswerDao {

    @Select("SELECT * FROM answer WHERE questionId = #{questionId} LIMIT #{page}, 6")
    List<Answer> selectByQuestionIdAndPage(@Param("questionId")int questionId, @Param("page")int page);

    @Select("SELECT authorId, text, nickName, icon, answer.id, answer.likes FROM answer JOIN user_info ON authorId = userId WHERE questionId = #{questionId} LIMIT #{page}, 6")
    List<AnswerWithUserInfo> selectAnswerByQuestionIdAndPage(@Param("questionId")int questionId, @Param("page")int page);

    @Select("SELECT authorId, text, nickName, icon, answer.id, answer.likes FROM answer JOIN user_info ON authorId = userId WHERE questionId = #{questionId} AND id > #{startId} LIMIT 6")
    List<AnswerWithUserInfo> selectAnswerByQuestionIdAndStartId(@Param("questionId")int questionId, @Param("startId") int startId);

    @Select("SELECT answer.id, likes, questionId, authorId, nickName, icon, text FROM answer JOIN user_info ON authorId = user_info.userId WHERE answer.id = #{aid}")
    AnswerWithUserInfo selectAnswerById(int aid);

    @Select("SELECT answer.id, likes, questionId, authorId, nickName, icon, text FROM answer JOIN user_info ON authorId = user_info.userId WHERE answer.id = #{aid} AND answer.questionId = #{qid}")
    AnswerWithUserInfo selectAnswerWithUserInfoByIdAndQid(@Param("aid") int aid, @Param("qid") int qid);

    @Select("SELECT id, likes, questionId, authorId, text FROM answer WHERE answer.id = #{id} AND authorId = #{uid}")
    Answer selectAnswerByIdAndUid(@Param("id") int id, @Param("uid") int uid);

    @Select("SELECT question.id AS id, question.title AS title, answer.id AS aid, answer.text AS text FROM answer JOIN question ON answer.questionId = question.id WHERE answer.authorId = #{uid} LIMIT #{page}, 6")
    List<HomePageQuestion> selectByAuthorId(@Param("uid") Integer uid, @Param("page") Integer page);

    @Select("SELECT question.id AS id, question.title AS title, answer.id AS aid, answer.text AS text FROM answer JOIN question ON answer.questionId = question.id WHERE answer.authorId = #{uid} AND answer.id > #{startId} LIMIT 6")
    @Results({
        @Result(property = "question.id", column = "id"),
        @Result(property = "question.title", column = "title"),
        @Result(property = "popularAnswer.id", column = "aid"),
        @Result(property = "popularAnswer.text", column = "text")
    })
    List<HomePageQuestion> selectByAuthorIdAndStartId(@Param("uid") Integer uid, @Param("startId") Integer startId);

    @Delete("DELETE FROM answer WHERE id = #{aid} AND authorId = #{uid}")
    int deleteByAid(@Param("aid")Integer aid, @Param("uid")Integer uid);

    @Update("UPDATE answer SET text = #{text} WHERE id = #{aid} AND authorId = #{uid}")
    int updateByAidAndUid(@Param("text")String text, @Param("aid")Integer aid, @Param("uid")Integer uid);

    @Update("UPDATE answer SET likes = likes + 1 WHERE id = #{aid}")
    void addLikes(Integer aid);

    @Update("UPDATE answer SET likes = likes - 1 WHERE id = #{aid}")
    void deleteLikes(Integer aid);

    @Insert("INSERT INTO answer(authorId, questionId, text) VALUES (#{authorId}, #{questionId}, #{text})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addAnswer(Answer answer);

    @Insert("INSERT INTO answer(authorId, questionId, text) VALUES (#{authorId}, #{questionId}, #{text})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addAnswerFromPendingAnswer(PendingAnswer pendingAnswer);
}
