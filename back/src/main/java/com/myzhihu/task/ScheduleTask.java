package com.myzhihu.task;

import com.myzhihu.aspect.DistributeScheduled;
import com.myzhihu.service.ImageService;
import com.myzhihu.service.QuestionService;
import com.myzhihu.service.impl.RedisBatchDeleteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    @Autowired
    private ImageService imageService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisBatchDeleteService redisBatchDeleteService;


    @Scheduled(cron = "0 0 6 * * *")
    public void cleanManagementListTask() {
        redisBatchDeleteService.deleteKeys("ReviewingUserInfos", "ReviewingQuestions", "ReviewingAnswers");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanExpiredImagesTask() {
        imageService.deleteExpiredImages();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void updateCommendListTask() {
        questionService.refreshQuestionList();
    }

}
