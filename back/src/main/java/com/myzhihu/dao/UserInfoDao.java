package com.myzhihu.dao;

import com.myzhihu.domain.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoDao {

    @Select("SELECT userid, nickname, icon, about, background FROM user_info WHERE userId = #{uid}")
    UserInfo getInfoByUid(Integer uid);

    @Insert("INSERT INTO user_info(userId, nickName, icon, about) VALUES (#{userId}, #{nickName}, #{icon}, #{about})")
    void addUserInfo(UserInfo userInfo);

    @Select("SELECT * FROM user_info WHERE userId = #{uid}")
    UserInfo selectById(Integer uid);

    @Update("UPDATE user_info SET nickName = #{userInfo.nickName}, icon = #{userInfo.icon}, about = #{userInfo.about}, background = #{userInfo.background} WHERE userId = #{uid}")
    void updateUserInfoByUid(@Param("userInfo") UserInfo userInfo,@Param("uid") Integer userId);

    @Select("SELECT * FROM user_info WHERE nickName LIKE CONCAT('%', #{keyword}, '%')")
    List<UserInfo> searchUsers(String keyword);

    @Update("UPDATE user_info SET nickName = #{nickName} WHERE userId = #{uid}")
    void updateNickNameByUid(@Param("nickName") String nickName,@Param("uid") Integer userId);

    @Update("UPDATE user_info SET icon = #{icon} WHERE userId = #{uid}")
    void updateIconByUid(@Param("icon") String icon,@Param("uid") Integer userId);

    @Update("UPDATE user_info SET about = #{about} WHERE userId = #{uid}")
    void updateAboutByUid(@Param("about") String about,@Param("uid") Integer userId);

    @Select("SELECT icon FROM user_info WHERE userId = #{uid}")
    String getIconByUid(@Param("uid") Integer uid);

    @Select("SELECT user_info.background FROM user_info WHERE userId")
    String getBackgroundByUid(@Param("uid") Integer uid);

}
