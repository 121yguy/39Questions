package com.myzhihu.service.impl;

import com.myzhihu.config.RabbitConfig;
import com.myzhihu.dao.NoticeDao;
import com.myzhihu.domain.dto.NoticeWrapper;
import com.myzhihu.domain.entity.Notice;
import com.myzhihu.enums.NoticeType;
import com.myzhihu.service.NoticeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    NoticeDao noticeDao;

    @Override
    public void send(Integer recipientId, String msg, NoticeType type) {
        Notice notice = new Notice();
        notice.setRecipientId(recipientId);
        notice.setOperateTime(new Date(System.currentTimeMillis()));
        notice.setContent(msg);
        NoticeWrapper wrapper = new NoticeWrapper(notice, type);
        rabbitTemplate.convertAndSend(RabbitConfig.MY_ZHI_HU_WEB_MESSAGE_EXCHANGE, RabbitConfig.RABBITMQ_DIRECT_ROUTING, wrapper);
    }

    @Override
    public Integer getNumbersOfUnreadNotices(int uid) {
        return noticeDao.selectNumsOfNotReadNotices(uid);
    }

    @Override
    public List<Notice> getNotices(int uid, int start) {
        return noticeDao.selectNotices(uid, start);
    }

    @Override
    public boolean updateOperateTime(int id, int uid) {
        Date time = new Date(System.currentTimeMillis());
        return noticeDao.updateOperateTime(time, id, uid);
    }

    @Override
    public boolean readAllNotices(int uid) {
        Date time = new Date(System.currentTimeMillis());
        return noticeDao.updateAllOperateTime(time, uid);
    }
}
