package com.myzhihu.service.impl;

import com.myzhihu.dao.UserDao;
import com.myzhihu.dao.UserInfoDao;
import com.myzhihu.domain.entity.User;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createUser(User user) {
         userDao.register(user);
         UserInfo userInfo = new UserInfo();
         userInfo.setUserId(user.getUserId());
         userInfo.setNickName("用户" + String.valueOf(UUID.randomUUID()).replaceAll("-", ""));
         userInfoDao.addUserInfo(userInfo);
    }
}
