package com.myzhihu.service.impl;

import com.myzhihu.aspect.DistributeScheduled;
import com.myzhihu.dao.FavoritesDao;
import com.myzhihu.dao.LikedDao;
import com.myzhihu.dao.QuestionDao;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.QuestionWithUserInfo;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    LikedDao likedDao;

    @Autowired
    FavoritesDao favoritesDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<HomePageQuestion> getHomePageQuestions(int uid, int page) {
        List<HomePageQuestion> questions;
        if (Boolean.FALSE.equals(redisTemplate.hasKey("QuestionList"))) {
            int start = page * 12;
            questions = questionDao.selectByPage2(start);
        }
        else {
            if (page >= redisTemplate.opsForList().size("QuestionList")) return List.of();
            questions = (List<HomePageQuestion>) redisTemplate.opsForList().index("QuestionList", page);
        }
//        if (uid == 0) return questions;
//        for (HomePageQuestion question : questions) {
//            question.setLiked(likedDao.isLike(question.getPopularAnswer().getId(), uid) != null);
//            question.setFavored(!favoritesDao.selectByUidAndAid(uid, question.getPopularAnswer().getId()).isEmpty());
//        }
        return questions;
    }

    @Override
    public List<HomePageQuestion> getRandomHomePageQuestions(String sessionId) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey("random_questions:" + sessionId))) {
            if (!createRandomQuestionList(sessionId)) return List.of();
        }
        int page = (Integer) Objects.requireNonNull(redisTemplate.opsForList().leftPop("random_questions:" + sessionId));
        redisTemplate.expire("random_questions:" + sessionId, 10, TimeUnit.MINUTES);
        return (List<HomePageQuestion>) redisTemplate.opsForList().index("QuestionList", page);
    }

    @Override
    public List<Question> getUserQuestions(int uid, int startId) {
        return questionDao.selectUserQuestionsByPage(startId, uid);
    }

    @Override
    public List<Question> getQuestionsWithoutAnswer(int startId) {
        return questionDao.searchQuestionsWithoutAnswer(startId);
    }

    @Override
    public QuestionWithUserInfo getQuestionWithUserInfoById(int id) {
        return questionDao.selectQuestionWithUserInfoById(id);
    }

    @Override
    @DistributeScheduled
    public void refreshQuestionList() {
        int n = 100; // 缓存的页数
        List<List<HomePageQuestion>> res = new ArrayList<>();

        // 不允许数量少于设定数量页面的存在
        int i = questionDao.countCommendableQuestions() / 12;

        // 允许数量少于设定数量页面的存在
//        int nums = questionDao.countCommendableQuestions();
//        int i = nums % 12 == 0 ? nums / 12 : nums / 12 + 1;

        if (i < n) {
            for (int j = 0; j < i; j++) {
                res.add(questionDao.selectByPage2(j * 12));
            }
        }
        else {
            List<Integer> list = IntStream.range(0, i).boxed().collect(Collectors.toList());
            Collections.shuffle(list);
            list.subList(0, n).forEach(x -> res.add(questionDao.selectByPage2(x * 12)));
        }
        if (!res.isEmpty()) {
            redisTemplate.delete("QuestionList");
            redisTemplate.opsForList().rightPushAll("QuestionList", res.toArray());
        }
    }

    private boolean createRandomQuestionList(String sessionId) {
        Long l = redisTemplate.opsForList().size("QuestionList");
        if (Objects.isNull(l)) return false;
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);
        redisTemplate.opsForList().rightPushAll("random_questions:" + sessionId, nums.toArray());
        return true;
    }
}
