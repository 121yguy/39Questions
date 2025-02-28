package com.myzhihu.service;

import com.myzhihu.domain.dto.Register;
import com.myzhihu.domain.entity.User;

public interface RegisterService {
    void createUser(User user) throws Exception;

}
