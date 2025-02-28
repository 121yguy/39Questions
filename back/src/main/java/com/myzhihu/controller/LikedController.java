package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.service.LikeService;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikedController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/like")
    public Result<Boolean> like(@RequestParam int aid, @CookieValue(value = "token", defaultValue = "")String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return likeService.like(uid, aid) ? Result.success(true) : Result.error("你已经点赞过了");
    }

    @DeleteMapping("/cancelLike")
    public Result<Boolean> cancelLike(@RequestParam int aid, @CookieValue(value = "token", defaultValue = "")String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return likeService.cancelLike(uid, aid) ? Result.success(true) : Result.error("你还没有点赞");
    }

}
