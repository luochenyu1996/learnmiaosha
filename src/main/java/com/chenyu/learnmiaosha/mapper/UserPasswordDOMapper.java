package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.UserPasswordDO;
import org.apache.ibatis.annotations.Mapper;


public interface UserPasswordDOMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(UserPasswordDO record);


    int insertSelective(UserPasswordDO record);


    UserPasswordDO selectByPrimaryKey(Integer id);

    UserPasswordDO selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserPasswordDO record);


    int updateByPrimaryKey(UserPasswordDO record);
}