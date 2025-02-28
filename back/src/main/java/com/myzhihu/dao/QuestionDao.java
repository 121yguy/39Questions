package com.myzhihu.dao;

import com.myzhihu.domain.dto.QuestionWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {

    @Select("SELECT * FROM question")
    List<Question> selectAll();

    @Select("SELECT * FROM question")
    List<HomePageQuestion> selectAll2();

    @Select("SELECT id, title FROM question LIMIT #{page}, 12")
    List<HomePageQuestion> selectByPage(int page);

    List<HomePageQuestion> selectByPage2(@Param("page") int start);

    @Select("SELECT id, title FROM question WHERE authorId = #{uid} AND id > #{startId} LIMIT 6")
    List<Question> selectUserQuestionsByPage(@Param("startId") int startId, @Param("uid") int uid);

    @Select("SELECT * FROM question WHERE id = #{id}")
    Question selectById(int id);

    @Select("SELECT authorId, title, text, nickName, icon FROM question JOIN user_info ON authorId = userId WHERE question.id = #{id}")
    QuestionWithUserInfo selectQuestionWithUserInfoById(int id);

    @Insert("INSERT INTO question(title, text, authorId) VALUES (#{title}, #{text}, #{authorId})")
    void addQuestion(Question question);

    @Select("SELECT id, title FROM question WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<HomePageQuestion> searchQuestions(@Param("keyword") String keyword);

    List<HomePageQuestion> searchQuestions2(@Param("keyword") String keyword); //从answer中搜索关键字

    List<HomePageQuestion> searchQuestions3(@Param("keyword") String keyword); //不从answer中搜索关键字

    @Select("SELECT * FROM question WHERE NOT EXISTS(SELECT 1 FROM answer WHERE answer.questionId = question.id) AND id > #{id} LIMIT 12")
    List<Question> searchQuestionsWithoutAnswer(@Param("id") int id);

    @Select("SELECT COUNT(1) FROM question WHERE EXISTS(SELECT 1 FROM answer WHERE answer.questionId = question.id)")
    int countCommendableQuestions();
}
