package com.myzhihu.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
public class Notice {
    private int id;
    private String content;
    private int recipientId;
    private Date operateTime;
    private boolean isRead;
}
