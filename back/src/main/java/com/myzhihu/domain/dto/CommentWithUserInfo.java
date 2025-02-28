package com.myzhihu.domain.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class CommentWithUserInfo {
    private Integer id;
    private String content;
    private Integer authorId;
    private Timestamp time;
    private String icon;
    private String nickName;
}
