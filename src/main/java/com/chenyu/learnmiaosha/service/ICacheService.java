package com.chenyu.learnmiaosha.service;

/**
 * @author chen yu
 * @create 2021-12-06 10:43
 */
public interface ICacheService {

    void setCommonCache(String key,Object value);


    Object getFromCommonCache(String key);
}
