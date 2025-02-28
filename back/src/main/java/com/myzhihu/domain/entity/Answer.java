package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class Answer {
    private Integer id;
    private String text;
    private Integer questionId;
    private Integer authorId;
    private Integer likes;
}
