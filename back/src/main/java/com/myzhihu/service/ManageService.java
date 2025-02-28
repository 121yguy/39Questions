package com.myzhihu.service;

import java.util.List;

public interface ManageService<T> {
    List<T> getList(Integer uid);

    boolean approve(Integer id, Integer uid);

    boolean reject(Integer id, Integer uid);

    void clear(Integer uid);

    boolean checkStatus(Integer uid);
}
