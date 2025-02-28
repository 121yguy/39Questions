package com.myzhihu.service;

import com.myzhihu.domain.dto.LoginUserDTO;
import com.myzhihu.domain.dto.Register;
import com.myzhihu.domain.entity.User;
import com.myzhihu.domain.entity.UserInfo;

public interface UserService {
    UserInfo login(LoginUserDTO loginUserDTO);
    boolean logout(int uid, String deviceId);
    int register(Register register);
    boolean checkEmail(String email);
    boolean checkToken(String token);
    boolean generateRegisterVerify(String email);
    boolean generateFindPasswordVerify(String email);
    boolean updatePassword(String email, String password, String verify);
}
