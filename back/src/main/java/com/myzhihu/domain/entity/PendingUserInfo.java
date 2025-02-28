package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class PendingUserInfo {
    private Integer userId;
    private String nickName;
    private String icon;
    private String about;
    private String background;
}
