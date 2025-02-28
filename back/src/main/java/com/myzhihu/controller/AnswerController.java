package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.AnswerWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.QuestionAndAnswer;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.service.AnswerService;
import com.myzhihu.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/answers")
@AllArgsConstructor
public class AnswerController {

    private AnswerService answerService;

    @GetMapping("/getAllByQuestionId/{id}/{startId}")
    public Result<List<AnswerWithUserInfo>> getAnswerByQuestionId(@PathVariable("id") int id, @PathVariable("startId")int startId, @CookieValue(value = "token", defaultValue = "") String token) {
        if (token.isEmpty()) {
            List<AnswerWithUserInfo> res = answerService.getAnswerByQuestionIdAndStartId(id, startId, 0);
            return Result.success(res);
        }
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        List<AnswerWithUserInfo> res = answerService.getAnswerByQuestionIdAndStartId(id, startId, uid);
        return Result.success(res);
    }

    @GetMapping("/getAnswerById/{aid}")
    public Result<AnswerWithUserInfo> getAnswerById(@PathVariable("aid") Integer aid) {
        AnswerWithUserInfo answer = answerService.getAnswerByAnswerId(aid);
        return answer != null ? Result.success(answer) : Result.error("不存在该回答");
    }

    @GetMapping("/getAnswersByUserId/{uid}/{startId}")
    public Result<List<HomePageQuestion>> getAnswersByUserId(@PathVariable("uid") Integer uid, @PathVariable("startId") Integer startId) {
        return Result.success(answerService.getAnswersByUserId(uid, startId));
    }

    @DeleteMapping("/deleteByAid/{aid}")
    public Result<Boolean> deleteByAid(@PathVariable("aid") Integer aid, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return answerService.deleteAnswerByAnswerId(aid, uid) ? Result.success(true) : Result.error("删除失败");
    }

    @GetMapping("/getQuestionAndAnswer")
    public Result<QuestionAndAnswer> getQuestionAndAnswer(@RequestParam("aid") Integer aid, @RequestParam("qid") Integer qid, @CookieValue(value = "token", defaultValue = "") String token) {
        if (token.isEmpty()) {
            QuestionAndAnswer res = answerService.getQuestionAndAnswer(aid, qid, 0);
            return res != null ? Result.success(res) : Result.error("该回答不存在");
        }
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        QuestionAndAnswer res = answerService.getQuestionAndAnswer(aid, qid, uid);
        return res != null ? Result.success(res) : Result.error("该回答不存在");
    }

}
