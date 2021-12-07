package com.chenyu.learnmiaosha.pojo.model;

import java.math.BigDecimal;

/**
 * 交易服务层 model
 *
 */
public class OrderModel {

    /**
     * 交易流水号
     *
     */
    private String id;

    /**
     *  产生该订单的用户id
     *
     */
    private Integer userId;

    /**
     * 商品id
     *
     */
    private Integer itemId;

    /**
     * 是否是秒杀方式
     *
     */
    private Integer promoId;

    /**
     * 商品的购买时候的价格
     *
     */
    private BigDecimal itemPrice;

    /**
     * 购买数量
     *
     */
    private Integer amount;

    /**
     * 购买总金额
     *
     */
    private BigDecimal orderPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", promoId=" + promoId +
                ", itemPrice=" + itemPrice +
                ", amount=" + amount +
                ", orderPrice=" + orderPrice +
                '}';
    }
}
