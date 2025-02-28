package com.myzhihu.service;

import com.myzhihu.domain.dto.CommentWithUserInfo;

import java.util.List;

public interface CommentService {
    List<CommentWithUserInfo> getComments(long startId);
    Integer addComment(String content, int uid);
    boolean deleteComment(int id, int uid);
}
