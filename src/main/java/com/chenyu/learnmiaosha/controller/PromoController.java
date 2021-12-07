package com.chenyu.learnmiaosha.controller;

import com.chenyu.learnmiaosha.CodeUtil;
import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.mq.MqProducer;
import com.chenyu.learnmiaosha.pojo.model.UserModel;
import com.chenyu.learnmiaosha.response.CommonReturnType;

import com.chenyu.learnmiaosha.service.IPromoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动
 *
 * @author chen yu
 * @create 2021-12-06 11:59
 */
@RestController
@RequestMapping("/promo")
public class PromoController extends BaseController {

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private IPromoService promoService;


    /**
     * 生成秒杀用的验证码
     */
    @GetMapping(value = "/verifycode")
    public void generateverifycode(HttpServletResponse response) throws BusinessException, IOException {
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能生成验证码");
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能生成验证码");
        }

        Map<String, Object> map = CodeUtil.generateCodeAndPic();
        redisTemplate.opsForValue().set(redisVerifyCodeKey(userModel), map.get("code"));
        redisTemplate.expire(redisVerifyCodeKey(userModel), 10, TimeUnit.MINUTES);
        System.out.println(map.get("code"));
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());
    }


    /**
     * 根据商品 id 发布秒杀活动
     */
    @PostMapping(value = "/publish")
    public CommonReturnType publish(@RequestParam(name = "id") Integer id) {
        promoService.publishPromo(id);
        return CommonReturnType.create(null);
    }


    /**
     * 生成秒杀令牌
     */
    @GetMapping(value = "/promotoken", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType promotoken(@RequestParam(name = "itemId") Integer itemId,
                                       @RequestParam(name = "promoId") Integer promoId) {


        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }

        //获取秒杀访问令牌
        String promoToken = promoService.generateSecondKillToken(promoId, itemId, userModel.getId());

        if (promoToken == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "生成令牌失败");
        }
        //返回对应的结果
        return CommonReturnType.create("promotoken:"+promoToken);
    }



    /**
     * 生成秒杀用验证码 redis 的key
     */
    private String redisVerifyCodeKey(UserModel userModel) {
        return Constant.VERIFY_CODE_PREFIX + userModel.getId();


    }

}
