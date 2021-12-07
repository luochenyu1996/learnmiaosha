package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.PromoDO;
import org.apache.ibatis.annotations.Mapper;


public interface PromoDOMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(PromoDO record);


    int insertSelective(PromoDO record);


    PromoDO selectByPrimaryKey(Integer id);

    PromoDO selectByItemId(Integer itemId);


    int updateByPrimaryKeySelective(PromoDO record);


    int updateByPrimaryKey(PromoDO record);
}