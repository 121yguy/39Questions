package com.myzhihu.consumer;

import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.LikedDao;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteLikeConsumer {

    @Autowired
    LikedDao likedDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(MQDeleteExchange.DELETE_LIKE_QUEUE),
                    exchange = @Exchange(MQDeleteExchange.DELETE_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void doDelete(int id) {
        likedDao.deleteLikesByAid(id);
    }
}
