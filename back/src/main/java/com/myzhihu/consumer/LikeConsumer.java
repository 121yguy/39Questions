package com.myzhihu.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.config.RabbitConfig;
import com.myzhihu.dao.AnswerDao;
import com.myzhihu.domain.dto.LikeEntity;
import com.myzhihu.domain.entity.Notice;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class LikeConsumer {

    @Autowired
    private AnswerDao answerDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(RabbitConfig.MY_ZHI_HU_LIKE_TOPIC),
                    exchange = @Exchange(RabbitConfig.MY_ZHI_HU_LIKE_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void process(Message message) throws JsonProcessingException {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        LikeEntity likeEntity = new ObjectMapper().readValue(json, LikeEntity.class);
        if (likeEntity.getLike()) {
            answerDao.addLikes(likeEntity.getAid());
        }
        else {
            answerDao.deleteLikes(likeEntity.getAid());
        }
    }
}
