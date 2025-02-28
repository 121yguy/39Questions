package com.myzhihu.service.impl;

import com.myzhihu.dao.SubscribeDao;
import com.myzhihu.domain.dto.UserInfoWithSubscribe;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscribeDao subscribeDao;

    @Override
    public int getNumsOfFansByUid(int uid) {
        return subscribeDao.getFans(uid);
    }

    @Override
    public int getNumsOfFollowingsByUid(int uid) {
        return subscribeDao.getFollowed(uid);
    }

    @Override
    public boolean isSubscribed(int uid, int followedId) {
        return subscribeDao.isSubscribe(uid, followedId) != null;
    }

    @Override
    public boolean subscribe(int uid, int followedId) {
        return subscribeDao.subscribe(followedId, uid) == 1;
    }

    @Override
    public boolean unsubscribe(int uid, int followedId) {
        return subscribeDao.cancelSubscribe(followedId, uid) == 1;
    }

    @Override
    public List<UserInfoWithSubscribe> getFansByUid(int uid, int followerId, int page) {
        List<UserInfo> fans = subscribeDao.getFansByFollowId(followerId, page);
        return useUserInfoWrapper(uid, fans);
    }

    @Override
    public List<UserInfoWithSubscribe> getFollowingsByUid(int uid, int followerId, int page) {
        List<UserInfo> followers = subscribeDao.getFollowersByFanId(followerId, page);
        return useUserInfoWrapper(uid, followers);
    }

    private List<UserInfoWithSubscribe> useUserInfoWrapper(int uid, List<UserInfo> followers) {
        List<UserInfoWithSubscribe> res = new ArrayList<>();
        for (UserInfo userInfo : followers) {
            UserInfoWithSubscribe userInfoWithSubscribe = new UserInfoWithSubscribe();
            userInfoWithSubscribe.setUserInfo(userInfo);
            boolean isSub = uid != 0 && subscribeDao.isSubscribe(uid, userInfo.getUserId()) != null;
            userInfoWithSubscribe.setSubscribe(isSub);
            res.add(userInfoWithSubscribe);
        }
        return res;
    }
}
