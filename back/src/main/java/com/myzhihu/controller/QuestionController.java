package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.QuestionWithUserInfo;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.QuestionService;
import com.myzhihu.service.impl.QuestionsSearchServiceImpl;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionsSearchServiceImpl questionsSearchService;

    @Autowired
    QuestionService questionService;

    @GetMapping("/getByPage2/{page}")
    public Result<List<HomePageQuestion>> getHomePageQuestionsByPage2(@CookieValue(value = "token", defaultValue = "") String token, @PathVariable("page")int page) {
        int uid = 0;
        if (!token.isEmpty()) {
            Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
            uid = (Integer) claims.get("user").asMap().get("userId");
        }
        return Result.success(questionService.getHomePageQuestions(uid, page));
    }

    @GetMapping("/getHomePageQuestions")
    public Result<List<HomePageQuestion>> getHomePageQuestions(@RequestParam String sessionId) {
        List<HomePageQuestion> res = questionService.getRandomHomePageQuestions(sessionId);
        return Result.success(res);
    }

    @GetMapping("/getUserQuestionByPage")
    public Result<List<Question>> getUserQuestionsByPage(@RequestParam int startId, @RequestParam int uid) {
        return Result.success(questionService.getUserQuestions(uid, startId));
    }

    @GetMapping("/getById/{id}")
    public Result<QuestionWithUserInfo> getQuestionById(@PathVariable("id")Integer id) {
        return Result.success(questionService.getQuestionWithUserInfoById(id));
    }

    @GetMapping("/search")
    public Result<List<HomePageQuestion>> SearchQuestions(@RequestParam String keywords, @RequestParam int page) {
        if (keywords.equals("%") || keywords.equals("_")) return Result.error("关键词不合法");
        List<HomePageQuestion> res = questionsSearchService.doSearch(keywords, page, 6);
        return Result.success(res);
    }

    @GetMapping("/getQuestionsWithoutAnswer")
    public Result<List<Question>> getQuestionsWithoutAnswer(@RequestParam("headId") int headId) {
        return Result.success(questionService.getQuestionsWithoutAnswer(headId));
    }
}
