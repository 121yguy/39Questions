package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.Notice;
import com.myzhihu.service.NoticeService;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/getNumbersOfUnreadNotices")
    public Result<Integer> getNumbersOfUnreadNotices(@CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return Result.success(noticeService.getNumbersOfUnreadNotices(uid));
    }

    @GetMapping("/getNotices")
    public Result<List<Notice>> getNotices(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("page") int page) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        int start = (page - 1) * 6;
        return Result.success(noticeService.getNotices(uid, start));
    }

    @PutMapping("/addReadTime")
    public Result<Boolean> updateOperateTime(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("id") int id) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return noticeService.updateOperateTime(id, uid) ? Result.success(true) : Result.error("这不是你的消息");
    }

    @PutMapping("/readAllNotices")
    public Result<Boolean> readAllNotices(@CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return noticeService.readAllNotices(uid) ? Result.success(true) : Result.error("暂时没有未读消息");
    }
}
