package com.chenyu.learnmiaosha.service;

import com.chenyu.learnmiaosha.pojo.model.OrderModel;

/**
 * @author chen yu
 * @create 2021-12-06 10:54
 */
public interface IOrderService {

    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId);
}
