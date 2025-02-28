package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.PendingAnswerWithQuestionTitle;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.PendingAnswer;
import com.myzhihu.service.DraftService;
import com.myzhihu.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/pendingAnswers")
public class PendingAnswerController {

    @Resource(name = "PendingAnswerService")
    DraftService<PendingAnswer, PendingAnswerWithQuestionTitle> pendingAnswerService;

    @PostMapping("/addAnswer")
    public Result<Boolean> addAnswer(@RequestBody PendingAnswer pendingAnswer, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return pendingAnswerService.addDraft(pendingAnswer, uid, true) ? Result.success(true) : Result.error("添加失败，请检查提交数据合法性!");
    }

    @PostMapping("/addUnsubmittedAnswer")
    public Result<Boolean> addUnsubmittedAnswer(@RequestBody PendingAnswer pendingAnswer, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return pendingAnswerService.addDraft(pendingAnswer, uid, false) ? Result.success(true) : Result.error("添加失败，请检查提交数据合法性!");
    }

    @PutMapping("/updateAnswer")
    public Result<Boolean> saveAnswer(String text, boolean status, int id, @CookieValue(value = "token", defaultValue = "") String token, String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        PendingAnswer pendingAnswer = new PendingAnswer();
        pendingAnswer.setId(id);
        pendingAnswer.setText(text);
        pendingAnswer.setReviewed(status);
        if (type.equals("submit")) return pendingAnswerService.submitDraft(pendingAnswer, uid) ? Result.success(true) : Result.error("提交失败，请检查提交数据合法性!");
        return pendingAnswerService.updateDraft(pendingAnswer, uid) ? Result.success(true) : Result.error("保存失败，请检查提交数据合法性!");
    }

    @GetMapping("/getPendingAnswersByUid")
    public Result<List<PendingAnswerWithQuestionTitle>> getPendingAnswersByUid(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("page") int page) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return Result.success(pendingAnswerService.getDraftsByUid(uid, page));
    }

    @DeleteMapping("/deletePendingAnswerById")
    public Result<Boolean> deletePendingAnswerById(@RequestParam("id") int id, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return pendingAnswerService.deleteDraftByIdAndUid(id, uid) ? Result.success(true) : Result.error("删除失败，请稍后再试");
    }

}
