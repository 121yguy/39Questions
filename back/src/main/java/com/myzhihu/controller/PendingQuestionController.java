package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.PendingQuestion;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.DraftService;
import com.myzhihu.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pendingQuestions")
public class PendingQuestionController {

    @Resource(name = "PendingQuestionService")
    DraftService<Question, PendingQuestion> pendingQuestionService;

    @PostMapping("/addQuestion")
    public Result<Boolean> addQuestion(@RequestBody Question question, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        pendingQuestionService.addDraft(question, uid, true);
        return Result.success(true);
    }

    @PostMapping("/addUnsubmittedQuestion")
    public Result<Boolean> addUnsubmittedQuestion(@RequestBody Question question, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        pendingQuestionService.addDraft(question, uid, false);
        return Result.success(true);
    }

    @PutMapping("/updateQuestion")
    public Result<Boolean> updateQuestion(@CookieValue(value = "token", defaultValue = "") String token, String title, String text, int id, String type) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        Question question = new Question();
        question.setTitle(title);
        question.setText(text);
        question.setId(id);
        if (type.equals("submit")) {
            return pendingQuestionService.submitDraft(question, uid) ? Result.success(true) : Result.error("提交失败!请检查数据合法性");
        }
        return pendingQuestionService.updateDraft(question, uid) ? Result.success(true) : Result.error("更新失败!请检查数据合法性");
    }

    @GetMapping("/getPendingQuestionsByUid")
    public Result<List<PendingQuestion>> getPendingQuestionsByUid(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("page") int page) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return Result.success(pendingQuestionService.getDraftsByUid(uid, page));
    }

    @DeleteMapping("/deletePendingQuestionsById")
    public Result<Boolean> deletePendingQuestionsByIdAndUid(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("id") int id) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return pendingQuestionService.deleteDraftByIdAndUid(id, uid) ? Result.success(true) : Result.error("删除失败!请稍后再试");
    }

}

