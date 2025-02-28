package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class PendingAnswer {
    private Integer id;
    private String text;
    private Integer questionId;
    private Integer authorId;
    private Boolean reviewed;
    private int relatedAnswer;
}
