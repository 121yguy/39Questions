package com.myzhihu.domain.dto;

import lombok.Data;

@Data
public class AnswerWithUserInfo {

    private Integer authorId;
    private String nickName;
    private String icon;
    private String text;
    private Integer id;
    private Integer questionId;
    private Integer likes;
    private boolean isLike;
    private boolean isFavorite;
}
