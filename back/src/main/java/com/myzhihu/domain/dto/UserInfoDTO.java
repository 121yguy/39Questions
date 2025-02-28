package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.UserInfo;
import lombok.Data;

@Data
public class UserInfoDTO {
    private UserInfo userInfo;
    private int fans;
    private int followers;
    private boolean isMe;
}
