package com.myzhihu.domain.dto;

import lombok.Data;

@Data
public class PendingAnswerWithQuestionTitle {
    private String title;
    private Integer id;
    private String text;
    private Integer questionId;
    private Integer authorId;
    private Boolean reviewed;
}
