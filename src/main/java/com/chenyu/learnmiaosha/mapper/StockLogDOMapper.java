package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.StockLogDO;
import org.apache.ibatis.annotations.Mapper;


public interface StockLogDOMapper {

    int deleteByPrimaryKey(String stockLogId);


    int insert(StockLogDO record);


    int insertSelective(StockLogDO record);

    StockLogDO selectByPrimaryKey(String stockLogId);


    int updateByPrimaryKeySelective(StockLogDO record);

    int updateByPrimaryKey(StockLogDO record);
}