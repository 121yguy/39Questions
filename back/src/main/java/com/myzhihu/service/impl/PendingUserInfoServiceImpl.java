package com.myzhihu.service.impl;

import com.myzhihu.dao.PendingUserInfoDao;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.DraftService;
import com.myzhihu.service.ImageService;
import com.myzhihu.utils.ImagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("pendingUserInfoService")
public class PendingUserInfoServiceImpl implements DraftService<UserInfo, Object> {

    @Autowired
    PendingUserInfoDao pendingUserInfoDao;

    @Autowired
    ImageService imageService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public boolean addDraft(UserInfo userInfo, int uid, boolean isSubmit) {
        if (pendingUserInfoDao.selectByUid(uid) != null) return false;
        userInfo.setUserId(uid);
        List<String> filenames = new ArrayList<>();
        if (!Objects.isNull(userInfo.getIcon()) && !userInfo.getIcon().isEmpty())
            filenames.add(ImagesUtils.getFilenameFromPath(userInfo.getIcon()));
        if (!Objects.isNull(userInfo.getBackground()) && !userInfo.getBackground().isEmpty())
            filenames.add(ImagesUtils.getFilenameFromPath(userInfo.getBackground()));
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                imageService.addReferenceCountByFilenames(filenames);
                pendingUserInfoDao.addPendingUserInfo(userInfo);
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.FALSE.equals(execute)) throw new RuntimeException();

        return true;
    }

    @Override
    public boolean updateDraft(UserInfo draft, int uid) {
        return false;
    }

    @Override
    public boolean submitDraft(UserInfo draft, int uid) {
        return false;
    }

    @Override
    public boolean deleteDraftByIdAndUid(int id, int uid) {
        return false;
    }

    @Override
    public List<Object> getDraftsByUid(int uid, int page) {
        return List.of();
    }
}
