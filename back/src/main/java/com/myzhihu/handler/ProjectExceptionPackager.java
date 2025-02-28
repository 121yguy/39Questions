package com.myzhihu.handler;

import com.myzhihu.exception.DaoException;
import com.myzhihu.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class ProjectExceptionPackager {
    @Pointcut("execution(* com.myzhihu.dao.*.*(..))")
    public void daoPt() {}

    @Pointcut("execution(* com.myzhihu.service..*(..))")
    public void servicePt() {}

    @Pointcut("execution(* com.myzhihu.controller.*.*(..))")
    public void controllerPt() {}

    @Around("daoPt()")
    public Object daoMethod (ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }catch (Exception e) {
            throw new DaoException("数据层异常:" + e.getMessage());
        }
    }

    @Around("servicePt()")
    public Object serviceMethod (ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }catch (Exception e) {
            throw new ServiceException("服务层异常:" + e.getMessage());
        }
    }
}
