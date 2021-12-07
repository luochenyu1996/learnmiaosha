package com.chenyu.learnmiaosha.service.impl;

import com.chenyu.learnmiaosha.service.ICacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.chenyu.learnmiaosha.constant.Constant.MEMORY_CACHE_TIME;

/**
 * @author chen yu
 * @create 2021-12-06 11:00
 */
@Service
public class CacheServiceImpl implements ICacheService {

    private Cache<String,Object> commonCache = null;

    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                //设置缓存中最大可以存储100个KEY,超过100个之后会按照LRU的策略移除缓存项
                .maximumSize(100)
                //设置写缓存后多少秒过期
                .expireAfterWrite(MEMORY_CACHE_TIME, TimeUnit.SECONDS).build();
    }


    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);

    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
