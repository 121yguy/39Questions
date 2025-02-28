package com.myzhihu.service.impl;

import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.AnswerDao;
import com.myzhihu.dao.FavoritesDao;
import com.myzhihu.dao.LikedDao;
import com.myzhihu.dao.QuestionDao;
import com.myzhihu.domain.dto.AnswerWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.QuestionAndAnswer;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.AnswerService;
import com.myzhihu.service.ImageService;
import com.myzhihu.utils.ImagesUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    ImageService imageService;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    LikedDao likedDao;

    @Autowired
    private FavoritesDao favoritesDao;


    @Override
    public List<AnswerWithUserInfo> getAnswerByQuestionIdAndStartId(int questionId, int startId, int userId) {
        if (userId == 0) {
            return answerDao.selectAnswerByQuestionIdAndStartId(questionId, startId);
        }
        List<AnswerWithUserInfo> answerWithUserInfos = answerDao.selectAnswerByQuestionIdAndStartId(questionId, startId);
        if (answerWithUserInfos.isEmpty())
            return List.of();
        List<Integer> answerIds = answerWithUserInfos.stream()
                .map(AnswerWithUserInfo::getId)
                .toList();
        Map<Integer, Boolean> likedMap = likedDao.batchIsLike(answerIds, userId);
        Map<Integer, Boolean> favoredMap = favoritesDao.batchSelectByUidAndAid(answerIds, userId);
        for (AnswerWithUserInfo answerWithUserInfo : answerWithUserInfos) {
            Integer answerId = answerWithUserInfo.getId();
            answerWithUserInfo.setLike(likedMap.containsKey(answerId));
            answerWithUserInfo.setFavorite(favoredMap.containsKey(answerId));
        }
        return answerWithUserInfos;
    }

    @Override
    public AnswerWithUserInfo getAnswerByAnswerId(int answerId) {
        return answerDao.selectAnswerById(answerId);
    }

    @Override
    public List<HomePageQuestion> getAnswersByUserId(int userId, int startId) {
        return answerDao.selectByAuthorIdAndStartId(userId, startId);
    }

    @Override
    public boolean deleteAnswerByAnswerId(int answerId, int userId) {
        String html = answerDao.selectAnswerById(answerId).getText();
        List<String> filenames = ImagesUtils.getFilenameFromHTML(html);
        if (answerDao.deleteByAid(answerId, userId) != 0) {
        imageService.reduceReferenceCountByFilenames(filenames);
        return true;
        }
        return false;
    }

    @Override
    public QuestionAndAnswer getQuestionAndAnswer(int answerId, int questionId, int userId) {
        AnswerWithUserInfo answer = answerDao.selectAnswerWithUserInfoByIdAndQid(answerId, questionId);
        if (Objects.isNull(answer))
            return null;
        Question question = questionDao.selectById(questionId);
        QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(question, answer);
        if (userId == 0)
            return questionAndAnswer;
        questionAndAnswer.getAnswer().setLike(likedDao.isLike(answer.getId(), userId) != null);
        questionAndAnswer.getAnswer().setFavorite(!favoritesDao.selectByUidAndAid(userId, answer.getId()).isEmpty());
        return questionAndAnswer;
    }
}
