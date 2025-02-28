package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/commons")
public class CommonController {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/updateToken")
    public ResponseEntity<Result<Boolean>> updateToken(@CookieValue(value = "token", defaultValue = "")String token) {
        try {
            Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
            Map<String, Object> userMap = claimMap.get("user").asMap();
            Integer uid = (Integer) userMap.get("userId");
            String deviceId = (String) userMap.get("deviceId");
            Long now = System.currentTimeMillis() / 1000;
            Long endTime = claimMap.get("exp").asLong();
            if (endTime - now < 3 * 24 * 3600) {
                redisTemplate.expire("uid:" + uid, 7, TimeUnit.DAYS);
                String newToken = JwtUtils.createJwtWithUidAndDeviceId(uid, deviceId);
                ResponseCookie cookie = ResponseCookie
                        .from("token", newToken)
//                        .httpOnly(true)
                        .secure(false)
                        .maxAge(7 * 24 * 3600)
                        .path("/")
                        .build();

                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(Result.success(true));
            }
            return ResponseEntity
                    .ok()
                    .body(Result.success(true));
        } catch (Exception e) {
            return ResponseEntity
                    .ok()
                    .body(Result.error("登录信息失效，请重新登录"));
        }
    }

}
