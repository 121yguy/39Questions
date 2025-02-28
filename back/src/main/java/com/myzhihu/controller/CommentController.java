package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.CommentWithUserInfo;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.service.CommentService;
import com.myzhihu.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentController {

    CommentService commentService;

    @GetMapping("/getComments")
    public Result<List<CommentWithUserInfo>> getComments(@RequestParam("startId") long startId) {
        return Result.success(commentService.getComments(startId));
    }

    @PostMapping("/addComment")
    public Result<Integer> addComment(String content, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        Integer id = commentService.addComment(content, uid);
        return id > 0 ? Result.success(id) : Result.error("添加失败");
    }

    @DeleteMapping("/deleteComment")
    public Result<Boolean> deleteComment(@RequestParam("id") int id, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return commentService.deleteComment(id, uid) ? Result.success(true) : Result.error("删除失败,这不是你的评论");
    }
}
