package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.User;
import lombok.Data;

@Data
public class LoginUserDTO {

    private User user;
    private String deviceId;

}
