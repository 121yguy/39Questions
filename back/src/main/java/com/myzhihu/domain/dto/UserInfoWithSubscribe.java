package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.UserInfo;
import lombok.Data;

@Data
public class UserInfoWithSubscribe {
    private UserInfo userInfo;
    private boolean subscribe;
}
