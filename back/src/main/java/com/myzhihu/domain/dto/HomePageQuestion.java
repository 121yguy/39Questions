package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class HomePageQuestion {
    private Question question;
    private Answer popularAnswer;
    private boolean liked;
    private boolean favored;
}
