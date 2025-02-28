package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAndAnswer {
    private Question question;
    private AnswerWithUserInfo answer;
}
