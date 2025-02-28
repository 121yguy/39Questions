package com.myzhihu.service.impl;

import com.myzhihu.constant.CommentForbiddenWords;
import com.myzhihu.dao.CommentDao;
import com.myzhihu.domain.dto.CommentWithUserInfo;
import com.myzhihu.domain.entity.Comment;
import com.myzhihu.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentServiceImpl implements CommentService {

    CommentDao commentDao;

    @Override
    public List<CommentWithUserInfo> getComments(long startId) {
        return commentDao.getCommentsByStartId(startId);
    }

    @Override
    public Integer addComment(String content, int uid) {
        if (content.length() > 100) return -1;
        content = forbiddenWordsFilter(content.trim());
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthorId(uid);
        comment.setTime(new Timestamp(System.currentTimeMillis()));
        commentDao.addComment(comment);
        return comment.getId();
    }

    @Override
    public boolean deleteComment(int id, int uid) {
        return commentDao.deleteCommentByIdAndUid(id, uid) != 0;
    }

    private String forbiddenWordsFilter(String content) {
        for (String word : CommentForbiddenWords.words) {
            String regex = Pattern.quote(word);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            content = matcher.replaceAll("*".repeat(word.length()));
        }
        return content;
    }
}
