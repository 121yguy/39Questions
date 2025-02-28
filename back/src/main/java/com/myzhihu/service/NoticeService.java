package com.myzhihu.service;

import com.myzhihu.domain.entity.Notice;
import com.myzhihu.enums.NoticeType;

import java.util.List;

public interface NoticeService {
    void send(Integer recipientId, String msg, NoticeType type);
    Integer getNumbersOfUnreadNotices(int uid);
    List<Notice> getNotices(int uid, int start);
    boolean updateOperateTime(int id, int uid);
    boolean readAllNotices(int uid);
}
