package com.myzhihu.filter;

import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.LoginUser;
import com.myzhihu.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token"))
                        token = cookie.getValue();
                }
            }
            if (!StringUtils.hasText(token)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                filterChain.doFilter(request, response);
                return;
            }
            Map<String, Claim> claims = JwtUtils.parseJwt(token);
            Map<String, Object> userMap = claims.get("user").asMap();
            Integer uid = (Integer) userMap.get("userId");
            String deviceId = (String) userMap.get("deviceId");

            LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get("uid:" + uid + "&deviceId:" + deviceId);
            if (loginUser == null){
                throw new RuntimeException("登录信息过期，请重新登录");
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result<Object> result = new Result<>(HttpStatus.UNAUTHORIZED.value(), null, e.getMessage());
            String json = new ObjectMapper().writeValueAsString(result);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(json);
        }
    }
}
