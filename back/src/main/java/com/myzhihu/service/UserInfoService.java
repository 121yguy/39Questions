package com.myzhihu.service;

import com.myzhihu.domain.dto.UserInfoDTO;
import com.myzhihu.domain.entity.UserInfo;

public interface UserInfoService {
    UserInfo getUserInfoByUid(int uid);
    /**
     * 该方法用来根据uid查询用户的详细信息，并通过与查询者uid作比较来判断该被查询用户是否是查询者本人
     * @param uid 被查询对象的uid
     * @param myUid 查询对象的uid
     * @return {@code UserInfoWithIsMe}类的对象或者{@code null},{@code UserInfoWithIsMe}类封装了{@code UserInfo}和布尔值{@code isMe}用来判断该被查询用户是否是查询者本人
     */
    UserInfoDTO getWrappedUserInfoByUid(int uid, int myUid);
}
