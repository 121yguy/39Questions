package com.myzhihu.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface AuthorizationDao {
    List<String> getAuthorizations(Integer userId);

    @Insert("INSERT INTO user_role(user_id, role_id) VALUES (#{userId}, 2)")
    void insertUserRole(@Param("userId") Integer userId);
}
