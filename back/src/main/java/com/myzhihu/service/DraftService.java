package com.myzhihu.service;

import java.util.List;

public interface DraftService<T, V> {
    boolean addDraft(T draft, int uid, boolean isSubmit);
    boolean updateDraft(T draft, int uid);
    boolean submitDraft(T draft, int uid);
    boolean deleteDraftByIdAndUid(int id, int uid);
    List<V> getDraftsByUid(int uid, int page);
}
