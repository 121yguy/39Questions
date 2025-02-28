package com.myzhihu.dao;

import com.myzhihu.domain.entity.PendingUserInfo;
import com.myzhihu.domain.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PendingUserInfoDao {

    @Select("SELECT nickName, icon, about, background, user_id AS userId FROM pending_user_info LIMIT #{start}, 20")
    List<PendingUserInfo> getPendingUserInfos(@Param("start") int start);

    @Insert("INSERT INTO pending_user_info(nickName, icon, about, user_id, background) VALUES (#{nickName}, #{icon}, #{about}, #{userId}, #{background})")
    void addPendingUserInfo(UserInfo userInfo);

//    @Update("UPDATE pending_user_info SET nickName = #{nickName}, icon = #{icon}, about = #{about} WHERE id = #{id}")
//    void updatePendingUserInfoById(@Param("userInfo") UserInfo userInfo, @Param("id") Integer id);

//    @Update("UPDATE pending_user_info SET reviewed = true WHERE id = #{id}")
//    int markAsReviewed(@Param("id") Integer id);

//    @Update("UPDATE pending_user_info SET reviewed = false WHERE id = #{id}")
//    void unmarkAsReviewed(@Param("id") Integer id);

    @Delete("DELETE FROM pending_user_info WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Integer userId);

    @Select("SELECT nickName, icon, about, user_id AS userId, background FROM pending_user_info WHERE user_id = #{uid}")
    PendingUserInfo selectByUid(@Param("uid") Integer uid);

}
