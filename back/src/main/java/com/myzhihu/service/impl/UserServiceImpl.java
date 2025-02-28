package com.myzhihu.service.impl;

import com.myzhihu.constant.RegisterCode;
import com.myzhihu.dao.AuthorizationDao;
import com.myzhihu.dao.UserDao;
import com.myzhihu.dao.UserInfoDao;
import com.myzhihu.domain.dto.LoginUserDTO;
import com.myzhihu.domain.dto.Register;
import com.myzhihu.domain.entity.LoginUser;
import com.myzhihu.domain.entity.User;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.UserService;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AuthorizationDao authorizationDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public UserInfo login(LoginUserDTO loginUserDTO) {
        User user = loginUserDTO.getUser();
        String deviceId = loginUserDTO.getDeviceId();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getAccount().trim(), user.getPass().trim());
        Authentication authenticate = authenticationManager.authenticate(authentication);
        if (Objects.isNull(authenticate)) return null;
        LoginUser login = (LoginUser) authenticate.getPrincipal();
        UserInfo userInfo = userInfoDao.getInfoByUid(login.getUser().getUserId());
        String key = "uid:" + login.getUser().getUserId() + "&deviceId:" + deviceId;
        redisTemplate.opsForValue().set(key, login);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        return userInfo;
    }

    @Override
    public boolean logout(int uid, String deviceId) {
        redisTemplate.delete("uid:" + uid + "&deviceId:" + deviceId);
        return true;
    }

    @Override
    public int register(Register register) {
        User user = register.getUser();
        if (user.getAccount().trim().length() < 3 || user.getPass().trim().length() < 8)
            return RegisterCode.ILLEGAL_DATA;
        try {
            String verify = (String) redisTemplate.opsForValue().get("Verify:" + user.getAccount());
            if (!Objects.equals(verify, register.getVerify()))
                return RegisterCode.ILLEGAL_DATA;
        } catch (NullPointerException e) {
            return RegisterCode.ILLEGAL_DATA;
        }

        String password = user.getPass().trim();
        password = new BCryptPasswordEncoder().encode(password);
        user.setPass(password);
        UserInfo userInfo = new UserInfo();

        userInfo.setNickName("用户" + String.valueOf(UUID.randomUUID()).replaceAll("-", ""));

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                userDao.register(user);
                userInfo.setUserId(user.getUserId());
                userInfoDao.addUserInfo(userInfo);
                authorizationDao.insertUserRole(user.getUserId());
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.FALSE.equals(execute)) return RegisterCode.ERROR;

        return RegisterCode.SUCCESS;
    }

    @Override
    public boolean checkEmail(String email) {
        User user = userDao.selectByAccount(email);
        return user == null;
    }

    @Override
    public boolean checkToken(String token) {
        if (token.isEmpty())
            return false;
        try {
            JwtUtils.getJwtPayload(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean generateRegisterVerify(String email) {
        if (!checkEmail(email))
            return false;
        return generateVerify(email);
    }

    @Override
    public boolean generateFindPasswordVerify(String email) {
        if (checkEmail(email))
            return false;
        return generateVerify(email);
    }

    @Override
    public boolean updatePassword(String email, String password, String verify) {
        if (!verify.equals(redisTemplate.opsForValue().get("Verify:" + email)))
            return false;
        password = new BCryptPasswordEncoder().encode(password);
        userDao.updatePass(password, email);
        //下线用户
        int uid = userDao.selectByAccount(email).getUserId();
        redisTemplate.delete("uid:" + uid);
        return true;
    }

    private boolean generateVerify(String email) {
        int verify = new Random().nextInt(8999) + 1000;
        emailService.sendEmail(email, "您的验证码是:", String.valueOf(verify));
        redisTemplate.opsForValue().set("Verify:" + email, String.valueOf(verify), 2, TimeUnit.MINUTES);
        return true;
    }
}
