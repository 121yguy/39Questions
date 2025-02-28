package com.myzhihu.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.domain.dto.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result;

        // 根据不同的异常类型返回不同的提示
        if (authException instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_OK);
            result = Result.error("登录失败，用户名或密码错误");
        } else {
            result = new Result<>(HttpServletResponse.SC_UNAUTHORIZED, null, "认证失败！请重新登录");
        }

        String json = new ObjectMapper().writeValueAsString(result);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(json);
    }
}
