package com.chenyu.learnmiaosha.service.impl;

import com.chenyu.learnmiaosha.Validator.ValidationResult;
import com.chenyu.learnmiaosha.Validator.ValidatorImpl;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.mapper.UserDOMapper;
import com.chenyu.learnmiaosha.mapper.UserPasswordDOMapper;
import com.chenyu.learnmiaosha.pojo.dao.UserDO;
import com.chenyu.learnmiaosha.pojo.dao.UserPasswordDO;
import com.chenyu.learnmiaosha.service.IUserService;
import com.chenyu.learnmiaosha.pojo.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 *
 * @author chen yu
 * @create 2021-12-05 12:07
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;


    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //对象转换
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已重复注册");
        }
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }


    /**
     * 根据手机号校验登录密码
     */
    @Override
    public UserModel validatePassword(String telephone, String encryptPassword) {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telephone);
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if (!StringUtils.equals(encryptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }


    /**
     * 通过用户id获取用户信息
     */
    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);
    }


    /**
     * 通过id 从本地缓存中获取用户信息
     */
    @Override
    public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate_" + id);
        if (userModel == null) {
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_" + id, userModel);
            redisTemplate.expire("user_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return userModel;
    }


    /**
     * 对象转换
     */
    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncryptPassword());
        }

        return userModel;
    }

    /**
     * 对象转化
     */
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);

        return userDO;
    }


    /**
     * 密码加密
     */
    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncryptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

}
