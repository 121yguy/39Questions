package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class Email {
    private String subject;
    private String text;
    private String recipient;
}
