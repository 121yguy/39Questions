package com.myzhihu.domain.dto;

import lombok.Data;

@Data
public class LikeEntity {
    private Boolean like;
    private Integer uid;
    private Integer aid;
}
