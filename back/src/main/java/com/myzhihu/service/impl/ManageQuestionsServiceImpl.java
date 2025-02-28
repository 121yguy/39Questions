package com.myzhihu.service.impl;

import com.myzhihu.dao.PendingQuestionDao;
import com.myzhihu.dao.QuestionDao;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.enums.NoticeType;
import com.myzhihu.service.ManageService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service("ManageQuestionsService")
public class ManageQuestionsServiceImpl implements ManageService<Question> {

    private static final String PERSONAL_KEY_HEAD = "question_reviewer:";
    private static final String PUBLIC_KEY_HEAD = "ReviewingQuestions";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    NoticeServiceImpl noticeService;

    @Autowired
    PendingQuestionDao pendingQuestionDao;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public List<Question> getList(Integer uid) {
        RLock lock = redissonClient.getLock(PUBLIC_KEY_HEAD + "Lock");

        try {
            boolean b = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!b) return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            int start = 0;
            List<Question> questions;
            List<Question> res = new ArrayList<>();
            if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0) {
                for (Object item : Objects.requireNonNull(redisTemplate.opsForList().range(PERSONAL_KEY_HEAD + uid, 0, -1))) {
                    int id = (int) item;
                    res.add(pendingQuestionDao.selectById(id));
                }
                return res;
            }
            while (!(questions = pendingQuestionDao.getQuestions(start)).isEmpty()) {
                for (Question question : questions) {
                    if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) >= 20) {
                        return res;
                    }
                    if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(PUBLIC_KEY_HEAD, question.getId()))) {
                        res.add(question);
                        redisTemplate.opsForSet().add(PUBLIC_KEY_HEAD, question.getId());
                        redisTemplate.opsForList().rightPush(PERSONAL_KEY_HEAD + uid, question.getId());
                    }
                }
                start += 20;
            }
            redisTemplate.expire(PERSONAL_KEY_HEAD + uid, 8, TimeUnit.HOURS);
            return res;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean approve(Integer id, Integer uid) {
        Question question = pendingQuestionDao.selectById(id);
        if (question == null)
            return false;

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                questionDao.addQuestion(question);
                pendingQuestionDao.deleteById(id);
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.FALSE.equals(execute)) return false;
        redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
        redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        noticeService.send(question.getAuthorId(), "您的问题已通过审核，详细请见:<a href=\"" + "/question/" + question.getId() + "\">戳我跳转~</a>", NoticeType.NORMAL);
        return true;
    }

    @Override
    public boolean reject(Integer id, Integer uid) {
        Question question = pendingQuestionDao.selectById(id);
        if (pendingQuestionDao.letQuestionReviewed(id) == 1) {
            redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
            redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
            noticeService.send(question.getAuthorId(), "您的解答未通过审核，请在稿件中心查看。", NoticeType.NORMAL);
            return true;
        }
        return false;
    }

    @Override
    public void clear(Integer uid) {
        while (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0) {
            Integer id = (Integer) redisTemplate.opsForList().leftPop(PERSONAL_KEY_HEAD + uid);
            redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        }
    }

    @Override
    public boolean checkStatus(Integer uid) {
        return Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0;
    }
}
