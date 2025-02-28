package com.myzhihu.consumer;

import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.FavoritesDao;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteFavorConsumer {

    @Autowired
    private FavoritesDao favoritesDao;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(MQDeleteExchange.DELETE_FAVOR_QUEUE),
                    exchange = @Exchange(MQDeleteExchange.DELETE_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void doDelete(int id) {
        favoritesDao.deleteFavoritesByFid(id);
    }
}
