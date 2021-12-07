package com.chenyu.learnmiaosha.service.impl;

import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.mapper.PromoDOMapper;
import com.chenyu.learnmiaosha.pojo.dao.PromoDO;
import com.chenyu.learnmiaosha.pojo.model.ItemModel;
import com.chenyu.learnmiaosha.pojo.model.PromoModel;
import com.chenyu.learnmiaosha.pojo.model.UserModel;
import com.chenyu.learnmiaosha.service.IItemService;
import com.chenyu.learnmiaosha.service.IPromoService;
import com.chenyu.learnmiaosha.service.IUserService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chenyu.learnmiaosha.constant.Constant.MULTIPLE_CROSS;

/**
 * @author chen yu
 * @create 2021-12-06 11:01
 */
@Service
public class PromoServiceImpl implements IPromoService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PromoDOMapper promoDOMapper;


    @Autowired
    private IItemService itemService;

    @Autowired
    private IUserService userService;


    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataobject->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        return promoModel;
    }


    /**
     * 发布活动（前提是活动表中有这个活动的信息因此最好写一个接口）
     *
     */
    @Override
    public void publishPromo(Integer promoId) {
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if(promoDO.getItemId() == null || promoDO.getItemId() == 0){
            return;
        }
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
        //将库存同步到redis内
        redisTemplate.opsForValue().set(redisStockKey(itemModel), itemModel.getStock());
        //将大闸的限制数字设到redis内
        redisTemplate.opsForValue().set(redisDoorKey(promoId),itemModel.getStock()* MULTIPLE_CROSS);
    }



    /**
     * 生成秒杀令牌
     *
     */
    @Override
    public String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId) {
        //判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
        if(redisTemplate.hasKey(redisStockInvalidKey(itemId))){
            return null;
        }
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel == null){
            return null;
        }
        //判断当前时间是否秒杀活动即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(Constant.PROMO_UNBEGIN);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(Constant.PROMO_END);
        }else{
            promoModel.setStatus(Constant.PROMO_BEGINNING);
        }
        //判断活动是否正在进行
        if(!promoModel.getStatus().equals(Constant.PROMO_BEGINNING)){
            return null;
        }
        //判断item信息是否存在
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if(itemModel == null){
            return null;
        }
        //判断用户信息是否存在
        UserModel userModel = userService.getUserByIdInCache(userId);
        if(userModel == null){
            return null;
        }
        //获取秒杀大闸的count数量
        long result = redisTemplate.opsForValue().increment(redisDoorKey(promoId),-1);
        if(result < 0){
            return null;
        }
        // 生成秒杀令牌  有效时间为5分钟
        String token = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(redisPromoTokenKey(promoId, itemId, userId),token);
        redisTemplate.expire(redisPromoTokenKey(promoId, itemId, userId),Constant.PROMO_TOKEN_REDIS_TIME, TimeUnit.MINUTES);
        return token;
    }


    /**
     * 对象转换
     *
     */
    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }


    /**
     * 生成redis 库存  key
     *
     */
    private String redisStockKey(ItemModel itemModel){
       return Constant.PROMO_STOCK_PREFIX +itemModel.getId();
    }


    /**
     * 生成 redis  秒杀大闸  key
     *
     */
    private String redisDoorKey(Integer promoId){
        return Constant.PROMO_DOOR_PREFIX +promoId;
    }



    /**
     * 生成 redis 为空的 key
     *
     */
    private String redisStockInvalidKey(Integer itemId){
        return Constant.PROMO_STOCK_INVALID_PREFIX +itemId;
    }

    /**
     * 生成秒杀令牌 key
     *
     */

    private String redisPromoTokenKey(Integer promoId, Integer itemId, Integer userId){
        return "promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId;
    }



}
