package com.chenyu.learnmiaosha.service;

import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.pojo.model.UserModel;

/**
 * 用户服务接口
 *
 * @author chen yu
 * @create 2021-12-05 12:06
 */
public interface IUserService {

    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);

    //通过缓存获取用户对象
    UserModel getUserByIdInCache(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /*
    telphone:用户注册手机
    password:用户加密后的密码
     */
    UserModel validatePassword(String telphone,String encrptPassword);


}
