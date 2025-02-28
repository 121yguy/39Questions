package com.myzhihu.dao;

import com.myzhihu.domain.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    @Select("SELECT * FROM user WHERE account = #{account} AND pass = #{pass}")
    User login(@Param("account")String account, @Param("pass")String pass);

    @Insert("INSERT INTO user(account, pass) VALUES (#{account}, #{pass})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void register(User user);

    @Select("SELECT * FROM user WHERE account = #{account}")
    User selectByAccount(String account);

    @Select("SELECT * FROM user")
    List<User> selectAll();

    @Update("UPDATE user SET pass = #{password} WHERE account = #{account}")
    void updatePass(@Param("password")String password, @Param("account")String account);

}
