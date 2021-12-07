package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.UserDO;
import org.apache.ibatis.annotations.Mapper;


public interface UserDOMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(UserDO record);


    int insertSelective(UserDO record);


    UserDO selectByPrimaryKey(Integer id);

    UserDO selectByTelphone(String telphone);

    int updateByPrimaryKeySelective(UserDO record);


    int updateByPrimaryKey(UserDO record);
}