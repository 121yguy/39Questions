package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class PendingQuestion {
    private Integer id;
    private String title;
    private Integer authorId;
    private String text;
    private Boolean reviewed;
}
