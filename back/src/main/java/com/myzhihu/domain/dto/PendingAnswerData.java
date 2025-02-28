package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.Answer;
import lombok.Data;

import java.util.List;

@Data
public class PendingAnswerData {
    private Answer answer;
    private String imagePaths;
}
