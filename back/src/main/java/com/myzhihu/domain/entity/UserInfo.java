package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Integer userId;
    private String nickName;
    private String icon;
    private String about;
    private String background;
}
