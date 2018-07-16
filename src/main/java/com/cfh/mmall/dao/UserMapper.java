package com.cfh.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.cfh.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    User selectByUserName(String userName);
    
    int userNameExsist(String userName);
    
    int emailExsist(String email);
    
    String queryQuestion(String userName);
    
    String queryAnswer(String userName);
    
    void modifyPassword(@Param("username") String userName,@Param("npassword") String newPassword);
}