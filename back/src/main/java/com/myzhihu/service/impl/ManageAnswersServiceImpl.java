package com.myzhihu.service.impl;

import com.myzhihu.dao.AnswerDao;
import com.myzhihu.dao.PendingAnswerDao;
import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.PendingAnswer;
import com.myzhihu.enums.NoticeType;
import com.myzhihu.service.ManageService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service("ManageAnswersService")
public class ManageAnswersServiceImpl implements ManageService<Answer> {

    private static final String PERSONAL_KEY_HEAD = "reviewer:";
    private static final String PUBLIC_KEY_HEAD = "ReviewingAnswers";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    NoticeServiceImpl noticeService;

    @Autowired
    PendingAnswerDao pendingAnswerDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public List<Answer> getList(Integer uid) {
        RLock lock = redissonClient.getLock(PUBLIC_KEY_HEAD + "Lock");

        try {
            boolean b = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!b) return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            int start = 0;
            List<Answer> answers;
            List<Answer> res = new ArrayList<>();
            if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0) {
                for (Object item : Objects.requireNonNull(redisTemplate.opsForList().range(PERSONAL_KEY_HEAD + uid, 0, -1))) {
                    int id = (int) item;
                    res.add(pendingAnswerDao.selectAsAnswerById(id));
                }
                return res;
            }
            while (!(answers = pendingAnswerDao.getAnswers(start)).isEmpty()) {
                for (Answer answer : answers) {
                    if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) >= 20) {
                        return res;
                    }
                    if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(PUBLIC_KEY_HEAD, answer.getId()))) {
                        res.add(answer);
                        redisTemplate.opsForSet().add(PUBLIC_KEY_HEAD, answer.getId());
                        redisTemplate.opsForList().rightPush(PERSONAL_KEY_HEAD + uid, answer.getId());
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
        PendingAnswer pendingAnswer = pendingAnswerDao.selectAsPendingAnswerById(id);
        if (pendingAnswer == null) return false;
        StringBuffer href = new StringBuffer();
        href.append("/question").append("/").append(pendingAnswer.getQuestionId()).append("/");
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                if (pendingAnswer.getRelatedAnswer() == 0) {
                    answerDao.addAnswerFromPendingAnswer(pendingAnswer);
                    href.append(pendingAnswer.getId());
                } else {
                    answerDao.updateByAidAndUid(pendingAnswer.getText(), pendingAnswer.getRelatedAnswer(), pendingAnswer.getAuthorId());
                    href.append(pendingAnswer.getRelatedAnswer());
                }
                pendingAnswerDao.deleteById(id);
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
            return true;
        });
        if (Boolean.FALSE.equals(execute)) return false;
        redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
        redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        noticeService.send(pendingAnswer.getAuthorId(), "您的解答已通过审核，详细请见:<a " + "href=\"" + href + "\">戳我跳转~</a>", NoticeType.NORMAL);
        noticeService.send(pendingAnswer.getAuthorId(), "您关注的人有新内容发布，快去围观吧。<a href=\"" + href + "\">戳我跳转~</a>", NoticeType.BROADCAST);
        return true;
    }

    @Override
    public boolean reject(Integer id, Integer uid) {
        Answer answer = pendingAnswerDao.selectAsAnswerById(id);
        if (pendingAnswerDao.letAnswerReviewed(id) == 1) {
            redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
            redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
            noticeService.send(answer.getAuthorId(), "您的解答未通过审核，请在稿件中心查看。", NoticeType.NORMAL);
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
