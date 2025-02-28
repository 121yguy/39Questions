package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.User;
import lombok.Data;

@Data
public class Register {

    private User user;

    private String verify;

}
