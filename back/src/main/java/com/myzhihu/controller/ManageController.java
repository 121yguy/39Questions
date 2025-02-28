package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.PendingUserInfo;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.ManageService;
import com.myzhihu.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/managements")
@PreAuthorize("hasAuthority('article:manage')")
public class ManageController {

    @Resource(name = "ManageQuestionsService")
    ManageService<Question> manageQuestionsService;

    @Resource(name = "ManageAnswersService")
    ManageService<Answer> manageAnswersService;

    @Resource(name = "ManageUserInfoService")
    ManageService<PendingUserInfo> manageUserInfoService;

    @PostMapping("/getList")
    public Result<List<?>> getList(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("type") String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        List<?> res = switch (type) {
            case "answers" -> manageAnswersService.getList(uid);
            case "questions" -> manageQuestionsService.getList(uid);
            default -> manageUserInfoService.getList(uid);
        };
        if (Objects.isNull(res)) return Result.error("系统正忙，请稍后再试");
        return Result.success(res);
    }

    @PostMapping("/approve")
    public Result<Boolean> approve(@RequestParam("id") int id, @CookieValue(value = "token", defaultValue = "") String token, @RequestParam("type") String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        boolean res = switch (type) {
            case "answers" -> manageAnswersService.approve(id, uid);
            case "questions" -> manageQuestionsService.approve(id, uid);
            default -> manageUserInfoService.approve(id, uid);
        };
        return res ? new Result<>(1, true, "") : new Result<>(0, false, "id无效!");
    }

    @PostMapping("/reject")
    public Result<Boolean> reject(@RequestParam("id") int id, @CookieValue(value = "token", defaultValue = "") String token, @RequestParam("type") String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        boolean res = switch (type) {
            case "answers" -> manageAnswersService.reject(id, uid);
            case "questions" -> manageQuestionsService.reject(id, uid);
            default -> manageUserInfoService.reject(id, uid);
        };
        return res ? new Result<>(1, true, "") : new Result<>(0, false, "id无效!");
    }

    @PostMapping("/finishReview")
    public Result<Boolean> finishReview(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("type") String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        switch (type) {
            case "answers": manageAnswersService.clear(uid);break;
            case "questions": manageQuestionsService.clear(uid);break;
            default: manageUserInfoService.clear(uid);
        }
        return new Result<>(1, true, "");
    }

    @GetMapping("/checkStatus")
    public Result<Boolean> checkStatus(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("type") String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        boolean res = switch (type) {
            case "answers" -> manageAnswersService.checkStatus( uid);
            case "questions" -> manageQuestionsService.checkStatus(uid);
            default -> manageUserInfoService.checkStatus(uid);
        };
        return res ? new Result<>(1, true, "") : new Result<>(0, false, "");
    }
}
