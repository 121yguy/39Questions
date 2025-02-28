package com.myzhihu.domain.dto;

import lombok.Data;

@Data
public class QuestionWithUserInfo {

    private Integer authorId;
    private String nickName;
    private String icon;
    private String title;
    private String text;
}
