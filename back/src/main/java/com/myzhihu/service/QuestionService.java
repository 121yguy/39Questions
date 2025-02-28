package com.myzhihu.service;

import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.QuestionWithUserInfo;
import com.myzhihu.domain.entity.Question;

import java.util.List;

public interface QuestionService {
    List<HomePageQuestion> getHomePageQuestions(int uid, int page);
    List<HomePageQuestion> getRandomHomePageQuestions(String sessionId);
    List<Question> getUserQuestions(int uid, int startId);
    List<Question> getQuestionsWithoutAnswer(int startId);
    QuestionWithUserInfo getQuestionWithUserInfoById(int id);
    void refreshQuestionList();
}
