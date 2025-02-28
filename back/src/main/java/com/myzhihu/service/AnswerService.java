package com.myzhihu.service;

import com.myzhihu.domain.dto.AnswerWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.QuestionAndAnswer;

import java.util.List;

public interface AnswerService {
    List<AnswerWithUserInfo> getAnswerByQuestionIdAndStartId(int questionId, int startId, int userId);
    AnswerWithUserInfo getAnswerByAnswerId(int answerId);
    List<HomePageQuestion> getAnswersByUserId(int userId, int startId);
    boolean deleteAnswerByAnswerId(int answerId, int userId);
    QuestionAndAnswer getQuestionAndAnswer(int answerId, int questionId, int userId);
}
