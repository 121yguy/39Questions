package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.Notice;
import com.myzhihu.enums.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeWrapper {
    private Notice notice;
    private NoticeType type;
}
