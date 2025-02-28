package com.myzhihu.dao;

import com.myzhihu.domain.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubscribeDao {
    @Select("SELECT COUNT(fan_id) FROM subscribe WHERE followed_id = #{uid}")
    int getFans(int uid);

    @Select("SELECT COUNT(followed_id) FROM subscribe WHERE fan_id = #{uid}")
    int getFollowed(int uid);

    @Select("SELECT followed_id FROM subscribe WHERE fan_id = #{fanId} AND followed_id = #{followedId}")
    Integer isSubscribe(@Param("fanId")Integer fanId, @Param("followedId")Integer followedId);

    @Insert("INSERT INTO subscribe(followed_id, fan_id) VALUES (#{followedId}, #{fanId})")
    int subscribe(@Param("followedId")int followedId, @Param("fanId")int fanId);

    @Delete("DELETE FROM subscribe WHERE followed_id = #{followedId} AND fan_id = #{fanId}")
    int cancelSubscribe(@Param("followedId")int followedId, @Param("fanId")int fanId);

    @Select("SELECT * FROM user_info WHERE userId IN (SELECT fan_id FROM subscribe WHERE followed_id = #{followerId}) LIMIT #{page}, 6")
    List<UserInfo> getFansByFollowId(@Param ("followerId") Integer followerId, @Param("page") Integer page);

    @Select("SELECT fan_id FROM subscribe WHERE followed_id = #{userId}")
    List<Integer> getFanIdsByUserId(@Param ("userId") Integer userId);

    @Select("SELECT * FROM user_info WHERE userId IN (SELECT followed_id FROM subscribe WHERE fan_id = #{fanId}) LIMIT #{page}, 6")
    List<UserInfo> getFollowersByFanId(@Param ("fanId")Integer fanId, @Param("page") Integer page);
}
