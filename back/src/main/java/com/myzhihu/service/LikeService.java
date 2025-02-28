package com.myzhihu.service;

public interface LikeService {
    boolean like(Integer uid, Integer aid);
    boolean cancelLike(Integer uid, Integer aid);
}
