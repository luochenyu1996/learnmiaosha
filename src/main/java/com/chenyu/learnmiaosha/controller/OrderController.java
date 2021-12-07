package com.chenyu.learnmiaosha.controller;

import com.chenyu.learnmiaosha.CodeUtil;
import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.mq.MqProducer;
import com.chenyu.learnmiaosha.pojo.model.UserModel;
import com.chenyu.learnmiaosha.response.CommonReturnType;
import com.chenyu.learnmiaosha.service.IItemService;
import com.chenyu.learnmiaosha.service.IPromoService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 订单Controller
 *
 * @author chen yu
 * @create 2021-12-05 11:04
 */

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private IItemService itemService;


    /**
     * google 的令牌桶
     *
     */
    private RateLimiter orderCreateRateLimiter;



    /**
     * 使用线程池作为泄洪队列
     *
     */
    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);
        //限流
        orderCreateRateLimiter = RateLimiter.create(Constant.RATELIMITER_COUNT);

    }




    /**
     * 活动下单接口
     *
     */
    @PostMapping(value = "/createorder",consumes={CONTENT_TYPE_FORMED})
    public CommonReturnType createOrder(@RequestParam(name="itemId")Integer itemId,
                                        @RequestParam(name="amount")Integer amount,
                                        @RequestParam(name="promoId",required = false)Integer promoId,
                                        @RequestParam(name="promoToken",required = false)String promoToken){

        if(!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMIT);
        }
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登陆，不能下单");
        }
        //获取用户的登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登陆，不能下单");
        }
        //校验秒杀令牌是否正确
        if(promoId != null){
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_"+promoId+"_userid_"+userModel.getId()+"_itemid_"+itemId);
            if(inRedisPromoToken == null){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
            }
            if(!org.apache.commons.lang3.StringUtils.equals(promoToken,inRedisPromoToken)){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
            }
        }
        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(() -> {
            //加入库存流水init状态
            String stockLogId = itemService.initStockLog(itemId,amount);
            //再去完成对应的下单事务型消息机制
            if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),itemId,promoId,amount,stockLogId)){
                throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"下单失败");
            }
            return null;
        });
        try {
            //等待
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        return CommonReturnType.create(null);
    }






}
