package com.chenyu.learnmiaosha.controller;

import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.pojo.viewobject.UserVO;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.response.CommonReturnType;
import com.chenyu.learnmiaosha.service.IUserService;
import com.chenyu.learnmiaosha.pojo.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户Controller
 *
 * @author chen yu
 * @create 2021-12-05 11:02
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {



    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    /**
     * 获取短信验证码
     *
     * done
     */
    @GetMapping(value = "/otp", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOpt(@RequestParam("telephone") String telephone) {
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        String optCode = String.valueOf(randomInt += 10000);
        httpServletRequest.getSession().setAttribute(telephone, optCode);
        System.out.println("telephone:" + telephone + "optCode:" + optCode);
        return CommonReturnType.create("optCode:"+optCode);
    }


    /**
     * 用户注册
     * todo 信息封装到Body中去
     */

    @PostMapping(value = "/register", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) {
        //验证二维码
        String optCodeInSession = (String) httpServletRequest.getSession().getAttribute(telephone);
        if (!otpCode.equals(optCodeInSession)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telephone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }


    /**
     * 用户登录
     *
     * done
     *
     * todo 参数放到Body中
     */
    @PostMapping(value = "/login",consumes={CONTENT_TYPE_FORMED})
    public CommonReturnType login(@RequestParam(name="telephone")String telephone,
                                  @RequestParam(name="password")String password)
            throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        UserModel userModel = userService.validatePassword(telephone,this.EncodeByMd5(password));
        //生成登录凭证token，UUID
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //把token存入到redis中
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);
        //下发了token
        return CommonReturnType.create("token:"+uuidToken);
    }




    /**
     * 根据id获取用户信息
     *  done
     *
     */
    @GetMapping("/get")
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);

        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO  = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }



    /**
     * 对象转换
     *
     * done
     *
     */
    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }



    /**
     * md5加密
     *
     */
    private String EncodeByMd5(String str) {
        String  encodedPassword=null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(Constant.MD5);
            BASE64Encoder base64en = new BASE64Encoder();
            encodedPassword = base64en.encode(md5.digest(str.getBytes(Constant.UTF_8)));
            return encodedPassword;

        }catch ( Exception e){
            e.printStackTrace();
        }
        return null;

    }


}
