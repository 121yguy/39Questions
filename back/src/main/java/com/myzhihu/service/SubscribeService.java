package com.myzhihu.service;

import com.myzhihu.domain.dto.UserInfoWithSubscribe;

import java.util.List;

public interface SubscribeService {
    int getNumsOfFansByUid(int uid);
    int getNumsOfFollowingsByUid(int uid);
    boolean isSubscribed(int uid, int followedId);
    boolean subscribe(int uid, int followedId);
    boolean unsubscribe(int uid, int followedId);
    List<UserInfoWithSubscribe> getFansByUid(int uid, int followerId, int page);
    List<UserInfoWithSubscribe> getFollowingsByUid(int uid, int followerId, int page);
}
