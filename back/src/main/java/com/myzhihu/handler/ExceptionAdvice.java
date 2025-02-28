package com.myzhihu.handler;

import com.myzhihu.domain.dto.Result;
import com.myzhihu.exception.DaoException;
import com.myzhihu.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(DaoException.class)
    public Result<Object> doDaoException(Exception e) throws Exception {
        if (e instanceof AuthenticationException ||
                e instanceof AccessDeniedException) {
            // 不做处理，让 Spring Security 默认处理这些异常
            throw e;
        }
        log.error(e.getMessage(), e);
        return Result.error("发生错误，请稍后再试");
    }

    @ExceptionHandler(ServiceException.class)
    public Result<Object> doServiceException(Exception e) throws Exception {
        if (e instanceof AuthenticationException ||
                e instanceof AccessDeniedException) {
            // 不做处理，让 Spring Security 默认处理这些异常
            throw e;
        }
        log.error(e.getMessage(), e);
        return Result.error("发生错误，请稍后再试");
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> doException(Exception e) throws Exception {
        if (e instanceof AuthenticationException ||
                e instanceof AccessDeniedException) {
            // 不做处理，让 Spring Security 默认处理这些异常
            throw e;
        }
        if (e instanceof MaxUploadSizeExceededException) {
            return Result.error("上传文件超出限制大小");
        }
        log.error(e.getMessage(), e);
        return Result.error("发生错误，请稍后再试");
    }
}
