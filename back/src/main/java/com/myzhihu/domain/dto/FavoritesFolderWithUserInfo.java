package com.myzhihu.domain.dto;

import lombok.Data;

@Data
public class FavoritesFolderWithUserInfo {
    private String name;
    private String nickName;
    private Integer userId;
    private String icon;
}
