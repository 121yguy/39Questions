package com.myzhihu.service.impl;

import com.myzhihu.config.RabbitConfig;
import com.myzhihu.dao.LikedDao;
import com.myzhihu.domain.dto.LikeEntity;
import com.myzhihu.service.LikeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    LikedDao likedDao;

    @Override
    public boolean like(Integer uid, Integer aid) {
        try {
            likedDao.like(aid, uid);
        } catch (Exception e) {
            return false;
        }
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.setLike(true);
        likeEntity.setUid(uid);
        likeEntity.setAid(aid);
        rabbitTemplate.convertAndSend(RabbitConfig.MY_ZHI_HU_LIKE_EXCHANGE, RabbitConfig.RABBITMQ_DIRECT_ROUTING, likeEntity);
        return true;
    }

    @Override
    public boolean cancelLike(Integer uid, Integer aid) {
        if (likedDao.cancelLike(aid, uid) != 1)
            return false;
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.setLike(false);
        likeEntity.setUid(uid);
        likeEntity.setAid(aid);
        rabbitTemplate.convertAndSend(RabbitConfig.MY_ZHI_HU_LIKE_EXCHANGE, RabbitConfig.RABBITMQ_DIRECT_ROUTING, likeEntity);
        return true;
    }
}
