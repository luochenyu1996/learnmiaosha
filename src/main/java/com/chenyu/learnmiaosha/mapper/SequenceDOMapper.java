package com.chenyu.learnmiaosha.mapper;


import com.chenyu.learnmiaosha.pojo.dao.SequenceDO;



public interface SequenceDOMapper {

    int deleteByPrimaryKey(String name);


    int insert(SequenceDO record);

    int insertSelective(SequenceDO record);

    SequenceDO selectByPrimaryKey(String name);

    SequenceDO getSequenceByName(String name);

    int updateByPrimaryKeySelective(SequenceDO record);


    int updateByPrimaryKey(SequenceDO record);
}