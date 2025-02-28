package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String account;
    private String pass;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", account='" + account + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
