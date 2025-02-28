package com.myzhihu.service.impl;

import com.myzhihu.dao.QuestionDao;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionsSearchServiceImpl implements SearchService<HomePageQuestion> {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<HomePageQuestion> doSearch(String keywords, int page, int numsPerPage) {
//        return searchFromQuestionTileAndAnswer(keywords, page, numsPerPage);
        return searchFromQuestionTile(keywords, page, numsPerPage);
    }

    @Deprecated
    private List<HomePageQuestion> searchFromQuestionTileAndAnswer(String keywords, int page, int numsPerPage) {
        List<HomePageQuestion> res = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        String[] keywordArray = keywords.split(" ");
        for (String keyword : keywordArray) {
            for (HomePageQuestion question : questionDao.searchQuestions2(keyword)) {
                if (!ids.contains(question.getQuestion().getId())) {
                    ids.add(question.getQuestion().getId());
                    res.add(question);
                }
            }
        }
        int start = page * numsPerPage;
        int end = Math.min((page + 1) * numsPerPage, res.size());
        if (start >= res.size())
            return null;
        return res.subList(start, end);
    }

    private List<HomePageQuestion> searchFromQuestionTile(String keywords, int page, int numsPerPage) {
        List<String> keywordList = this.keywordFilter(keywords);
        if (keywordList.isEmpty()) return Collections.emptyList();
        for (String keyword : keywordList) {
            String key = "Search:Q:" + keyword;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.expire(key, 10, TimeUnit.MINUTES);
                return (List<HomePageQuestion>) redisTemplate.opsForList().index(key, page);
            }
        }
        List<List<HomePageQuestion>> list = new ArrayList<>();
        String keyword = keywordList.get(0);
        String key = "Search:Q:" + keyword;
        List<HomePageQuestion> questions = questionDao.searchQuestions3(keyword);
        for (int i = 0; i < questions.size(); i += numsPerPage) {
            list.add(new ArrayList<>(questions.subList(i, Math.min(questions.size(), i + numsPerPage))));
        }
        redisTemplate.opsForList().rightPushAll(key, list.toArray());
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        return (List<HomePageQuestion>) redisTemplate.opsForList().index(key, page);
    }

}
