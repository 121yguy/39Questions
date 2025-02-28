package com.myzhihu.service.impl;

import com.myzhihu.dao.UserInfoDao;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserSearchServiceImpl implements SearchService<UserInfo> {

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<UserInfo> doSearch(String keywords, int page, int numsPerPage) {
//        return searchUserInfos(keywords, page, numsPerPage);
        return searchFromUsername(keywords, page, numsPerPage);
    }

    @Deprecated
    private List<UserInfo> searchUserInfos(String keywords, int page, int numsPerPage) {
        List<UserInfo> res = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        String[] keywordArray = keywords.split(" ");
        for (String keyword : keywordArray) {
            for(UserInfo userInfo : userInfoDao.searchUsers(keyword)) {
                if (!ids.contains(userInfo.getUserId())) {
                    ids.add(userInfo.getUserId());
                    res.add(userInfo);
                }
            }
        }
        int start = page * numsPerPage;
        int end = Math.min((page + 1) * numsPerPage, res.size());
        if (start >= res.size())
            return null;
        return res.subList(start, end);
    }

    private List<UserInfo> searchFromUsername(String keywords, int page, int numsPerPage) {
        List<String> keywordList = this.keywordFilter(keywords);
        if (keywordList.isEmpty()) return Collections.emptyList();
        for (String keyword : keywordList) {
            String key = "Search:U:" + keyword;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.expire(key, 10, TimeUnit.MINUTES);
                return (List<UserInfo>) redisTemplate.opsForList().index(key, page);
            }
        }
        List<List<UserInfo>> list = new ArrayList<>();
        String keyword = keywordList.get(0);
        String key = "Search:U:" + keyword;
        List<UserInfo> userInfos = userInfoDao.searchUsers(keyword);
        for (int i = 0; i < userInfos.size(); i += numsPerPage) {
            list.add(new ArrayList<>(userInfos.subList(i, Math.min(userInfos.size(), i + numsPerPage))));
        }
        redisTemplate.opsForList().rightPushAll(key, list.toArray());
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        return (List<UserInfo>) redisTemplate.opsForList().index(key, page);
    }
}
