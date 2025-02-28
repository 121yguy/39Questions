package com.myzhihu.service.impl;

import com.myzhihu.aspect.DistributeScheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisBatchDeleteService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @DistributeScheduled
    public void deleteKeys(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }
}
