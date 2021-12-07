package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.OrderDO;

public interface OrderDOMapper {

    int deleteByPrimaryKey(String id);


    int insert(OrderDO record);


    int insertSelective(OrderDO record);


    OrderDO selectByPrimaryKey(String id);


    int updateByPrimaryKeySelective(OrderDO record);


    int updateByPrimaryKey(OrderDO record);
}