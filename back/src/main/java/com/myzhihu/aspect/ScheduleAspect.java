package com.myzhihu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class ScheduleAspect {

    @Pointcut("@annotation(com.myzhihu.aspect.DistributeScheduled)")
    public void DistributeScheduledPt() {}

    @Around("DistributeScheduledPt()")
    public void method(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("任务执行异常", throwable);
            throw throwable;
        }
        long duration = (System.currentTimeMillis() - startTime);
        log.info("任务:{}于{}ms内完成", methodName, duration);
    }

}
