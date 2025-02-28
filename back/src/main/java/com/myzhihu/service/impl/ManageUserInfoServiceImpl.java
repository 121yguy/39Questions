package com.myzhihu.service.impl;

import com.myzhihu.dao.PendingUserInfoDao;
import com.myzhihu.dao.UserInfoDao;
import com.myzhihu.domain.entity.PendingUserInfo;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.enums.NoticeType;
import com.myzhihu.service.ImageService;
import com.myzhihu.service.ManageService;
import com.myzhihu.utils.ImagesUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service("ManageUserInfoService")
public class ManageUserInfoServiceImpl implements ManageService<PendingUserInfo> {

    private static final String PERSONAL_KEY_HEAD = "user_info_reviewer:";
    private static final String PUBLIC_KEY_HEAD = "ReviewingUserInfos";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    NoticeServiceImpl noticeService;

    @Autowired
    PendingUserInfoDao pendingUserInfoDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    ImageService imageService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public List<PendingUserInfo> getList(Integer uid) {
        RLock lock = redissonClient.getLock(PUBLIC_KEY_HEAD + "Lock");

        try {
            boolean b = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!b) return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            int start = 0;
            List<PendingUserInfo> pendingUserInfos;
            List<PendingUserInfo> res = new ArrayList<>();
            if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0) {
                for (Object item : Objects.requireNonNull(redisTemplate.opsForList().range(PERSONAL_KEY_HEAD + uid, 0, -1))) {
                    int userId = (int) item;
                    res.add(pendingUserInfoDao.selectByUid(userId));
                }
                return res;
            }
            while (!(pendingUserInfos = pendingUserInfoDao.getPendingUserInfos(start)).isEmpty()) {
                for (PendingUserInfo userInfo : pendingUserInfos) {
                    if (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) >= 20) {
                        return res;
                    }
                    if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(PUBLIC_KEY_HEAD, userInfo.getUserId()))) {
                        res.add(userInfo);
                        redisTemplate.opsForSet().add(PUBLIC_KEY_HEAD, userInfo.getUserId());
                        redisTemplate.opsForList().rightPush(PERSONAL_KEY_HEAD + uid, userInfo.getUserId());
                    }
                }
                start += 20;
            }
            redisTemplate.expire(PERSONAL_KEY_HEAD + uid, 8, TimeUnit.HOURS);
            return res;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean approve(Integer id, Integer uid) {
        PendingUserInfo pendingUserInfo = pendingUserInfoDao.selectByUid(id);
        if (pendingUserInfo == null)
            return false;

        UserInfo userInfo = userInfoDao.getInfoByUid(pendingUserInfo.getUserId());
        List<String> pendingDeleteImages = new ArrayList<>();
        Optional.ofNullable(pendingUserInfo.getNickName()).ifPresent(userInfo::setNickName);
        Optional.ofNullable(pendingUserInfo.getAbout()).ifPresent(userInfo::setAbout);
        Optional.ofNullable(pendingUserInfo.getIcon()).ifPresent(icon -> {
            pendingDeleteImages.add(ImagesUtils.getFilenameFromPath(userInfo.getIcon()));
            userInfo.setIcon(icon);
        });
        Optional.ofNullable(pendingUserInfo.getBackground()).ifPresent(background -> {
            pendingDeleteImages.add(ImagesUtils.getFilenameFromPath(userInfo.getBackground()));
            userInfo.setBackground(background);
        });

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                userInfoDao.updateUserInfoByUid(userInfo, pendingUserInfo.getUserId());
                pendingUserInfoDao.deleteByUserId(id);
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.FALSE.equals(execute)) return false;

        imageService.reduceReferenceCountByFilenames(pendingDeleteImages);
        redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
        redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        noticeService.send(id, "您提交的新个人资料已通过审核", NoticeType.NORMAL);
        return true;
    }

    @Override
    public boolean reject(Integer id, Integer uid) {
        PendingUserInfo pendingUserInfo = pendingUserInfoDao.selectByUid(id);
        pendingUserInfoDao.deleteByUserId(id);
        ArrayList<String> pendingDeleteImages = new ArrayList<>();
        Optional.ofNullable(pendingUserInfo.getIcon()).ifPresent(image ->
                pendingDeleteImages.add(ImagesUtils.getFilenameFromPath(image))
        );
        Optional.ofNullable(pendingUserInfo.getBackground()).ifPresent(image ->
                pendingDeleteImages.add(ImagesUtils.getFilenameFromPath(image))
        );
        imageService.reduceReferenceCountByFilenames(pendingDeleteImages);
        redisTemplate.opsForList().remove(PERSONAL_KEY_HEAD + uid, 0, id);
        redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        noticeService.send(id, "您提交的新个人资料未通过审核", NoticeType.NORMAL);
        return true;
    }

    @Override
    public void clear(Integer uid) {
        while (Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0) {
            Integer id = (Integer) redisTemplate.opsForList().leftPop(PERSONAL_KEY_HEAD + uid);
            redisTemplate.opsForSet().remove(PUBLIC_KEY_HEAD, id);
        }
    }

    @Override
    public boolean checkStatus(Integer uid) {
        return Objects.requireNonNull(redisTemplate.opsForList().size(PERSONAL_KEY_HEAD + uid)) != 0;
    }
}
