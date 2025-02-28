package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.dto.UserInfoWithSubscribe;
import com.myzhihu.service.SubscribeService;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    @GetMapping("/getFansByUid/{uid}")
    public Result<Integer> getFansByUid(@PathVariable("uid")Integer uid) {
        return Result.success(subscribeService.getNumsOfFansByUid(uid));
    }

    @GetMapping("/getFollowedByUid/{uid}")
    public Result<Integer> getFollowedByUid(@PathVariable("uid")Integer uid) {
        return Result.success(subscribeService.getNumsOfFollowingsByUid(uid));
    }

    @GetMapping("/isSubscribe/{followedId}")
    public Result<Boolean> isSubscribe(@PathVariable("followedId") Integer followedId, @CookieValue(value = "token", defaultValue = "")String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer fanId = (Integer) claims.get("user").asMap().get("userId");
        return Result.success(subscribeService.isSubscribed(fanId, followedId));
    }

    @PostMapping()
    public Result<Boolean> subscribe(@RequestParam int followedId, @CookieValue(value = "token", defaultValue = "")String token) {
        if (token.isEmpty()) return Result.error("请登录后再进行此操作");
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer fanId = (Integer) claims.get("user").asMap().get("userId");
        if (fanId == followedId) return Result.error("你不能关注自己");
        return subscribeService.subscribe(fanId, followedId) ? Result.success(true) : Result.error("你已经关注过TA了");
    }

    @DeleteMapping()
    public Result<Boolean> cancelSubscribe(@RequestParam int followedId, @CookieValue(value = "token", defaultValue = "")String token) {
        if (token.isEmpty()) return Result.error("请登录后再进行此操作");
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer fanId = (Integer) claims.get("user").asMap().get("userId");
        if (fanId == followedId) return Result.error("你不能对自己进行此操作");
        return subscribeService.unsubscribe(fanId, followedId) ? Result.success(true) : Result.error("你还没有TA");
    }

    @GetMapping("/getFansList")
    public Result<List<UserInfoWithSubscribe>> getFansList(@RequestParam("page") Integer page, @RequestParam("uid") Integer uid, @CookieValue(value = "token", defaultValue = "") String token) {
        int userId = 0;
        if (token != null) {
            Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
            userId = (Integer) claims.get("user").asMap().get("userId");
        }
        return Result.success(subscribeService.getFansByUid(userId, uid, page));
    }

    @GetMapping("/getFollowersList")
    public Result<List<UserInfoWithSubscribe>> getFlowersList(@RequestParam("page") Integer page, @RequestParam("uid") Integer uid, @CookieValue(value = "token", defaultValue = "") String token) {
        int userId = 0;
        if (token != null) {
            Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
            userId = (Integer) claims.get("user").asMap().get("userId");
        }
        return Result.success(subscribeService.getFollowingsByUid(userId, uid, page));
    }
}
