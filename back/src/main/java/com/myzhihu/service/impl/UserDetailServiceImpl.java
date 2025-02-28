package com.myzhihu.service.impl;

import com.myzhihu.dao.AuthorizationDao;
import com.myzhihu.dao.UserDao;
import com.myzhihu.domain.entity.LoginUser;
import com.myzhihu.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthorizationDao authorizationDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.selectByAccount(username);
        if (user == null) throw new UsernameNotFoundException("账号或密码错误");
        return new LoginUser(user, authorizationDao.getAuthorizations(user.getUserId()));
    }
}
