package com.myzhihu.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comment {
    private Integer id;
    private String content;
    private Integer authorId;
    private Timestamp time;
}
