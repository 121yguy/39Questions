package com.myzhihu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduleConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);  // 设置线程池大小
        scheduler.setThreadNamePrefix("scheduled-task-"); // 设置线程名称前缀
        scheduler.setAwaitTerminationSeconds(120); // 线程关闭前最大等待时间
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 关闭时等待任务完成
        return scheduler;
    }

}
