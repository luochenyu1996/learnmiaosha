package com.chenyu.learnmiaosha.service;

import com.chenyu.learnmiaosha.pojo.model.PromoModel;

/**
 * @author chen yu
 * @create 2021-12-06 10:41
 */
public interface IPromoService {


    PromoModel getPromoByItemId(Integer itemId);


    void publishPromo(Integer promoId);

    //生成秒杀用的令牌
    String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId);
}
