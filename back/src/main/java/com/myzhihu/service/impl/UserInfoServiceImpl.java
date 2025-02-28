package com.myzhihu.service.impl;

import com.myzhihu.dao.SubscribeDao;
import com.myzhihu.dao.UserInfoDao;
import com.myzhihu.domain.dto.UserInfoDTO;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    SubscribeDao subscribeDao;

    @Override
    public UserInfo getUserInfoByUid(int uid) {
        return userInfoDao.getInfoByUid(uid);
    }

    @Override
    public UserInfoDTO getWrappedUserInfoByUid(int uid, int myUid) {
        UserInfo userInfo = userInfoDao.selectById(uid);
        if (userInfo == null) return null;
        int fans = subscribeDao.getFans(uid);
        int followed = subscribeDao.getFollowed(uid);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setFans(fans);
        userInfoDTO.setFollowers(followed);
        userInfoDTO.setUserInfo(userInfo);
        if (myUid == 0) userInfoDTO.setMe(false);
        else userInfoDTO.setMe(uid == myUid);
        return userInfoDTO;
    }
}
