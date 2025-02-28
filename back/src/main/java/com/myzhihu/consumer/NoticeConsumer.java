package com.myzhihu.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.config.RabbitConfig;
import com.myzhihu.dao.NoticeDao;
import com.myzhihu.dao.SubscribeDao;
import com.myzhihu.domain.dto.NoticeWrapper;
import com.myzhihu.enums.NoticeType;
import com.myzhihu.service.impl.NoticeServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

@Component
public class NoticeConsumer {

    @Autowired
    NoticeDao noticeDao;

    @Autowired
    SubscribeDao subscribeDao;

    @Autowired
    NoticeServiceImpl noticeService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(RabbitConfig.MY_ZHI_HU_WEB_MESSAGE_TOPIC),
                    exchange = @Exchange(RabbitConfig.MY_ZHI_HU_WEB_MESSAGE_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void notice(Message message) throws JsonProcessingException {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        NoticeWrapper wrapper = new ObjectMapper().readValue(json, NoticeWrapper.class);
        if (wrapper.getType().equals(NoticeType.NORMAL)) {
            wrapper.getNotice().setOperateTime(new Date(System.currentTimeMillis()));
            noticeDao.addNotice(wrapper.getNotice());
        }
        if (wrapper.getType().equals(NoticeType.BROADCAST)) {
            List<Integer> fanIds = subscribeDao.getFanIdsByUserId(wrapper.getNotice().getRecipientId());
            for (Integer fanId : fanIds) {
                noticeService.send(fanId, wrapper.getNotice().getContent(), NoticeType.NORMAL);
            }
        }
    }
}
