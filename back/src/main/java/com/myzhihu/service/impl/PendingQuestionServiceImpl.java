package com.myzhihu.service.impl;

import com.myzhihu.dao.PendingQuestionDao;
import com.myzhihu.domain.entity.PendingQuestion;
import com.myzhihu.domain.entity.Question;
import com.myzhihu.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("PendingQuestionService")
public class PendingQuestionServiceImpl implements DraftService<Question, PendingQuestion> {

    @Autowired
    private PendingQuestionDao pendingQuestionDao;

    @Override
    public boolean addDraft(Question draft, int uid, boolean isSubmit) {
        if (draft.getTitle().trim().length() < 5 || draft.getTitle().length() > 50) return false;
        draft.setAuthorId(uid);
        if (isSubmit && !pendingQuestionDao.addPendingQuestionAndSubmit(draft)) return false;
        else return isSubmit || pendingQuestionDao.addPendingQuestion(draft);
    }

    @Override
    public boolean updateDraft(Question draft, int uid) {
        if (draft.getTitle().trim().length() < 5 || draft.getTitle().length() > 50) return false;
        return pendingQuestionDao.updateQuestionByIdAndUid(draft.getTitle(), draft.getText(), draft.getId(), uid) == 1;
    }

    @Override
    public boolean submitDraft(Question draft, int uid) {
        if (draft.getTitle().trim().length() < 5 || draft.getTitle().length() > 50) return false;
        if (!updateDraft(draft, uid))
            return false;
        pendingQuestionDao.letQuestionUnreviewed(draft.getId());
        return true;
    }

    @Override
    public boolean deleteDraftByIdAndUid(int id, int uid) {
        return pendingQuestionDao.deleteByIdAndUid(id, uid) == 1;
    }

    @Override
    public List<PendingQuestion> getDraftsByUid(int uid, int page) {
        int start = page * 12;
        return pendingQuestionDao.selectByUid(uid, start);
    }
}
