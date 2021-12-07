package com.chenyu.learnmiaosha.constant;

import com.google.common.util.concurrent.RateLimiter;

import javax.swing.text.TabExpander;

/**
 * 系统中的常量
 *
 * @author chen yu
 * @create 2021-12-05 11:57
 */
public class Constant {

    /**
     * 编码方式
     */
    public final static String UTF_8 = "utf-8";

    /**
     * 加密方式
     */
    public final static String MD5 = "MD5";

    /**
     * 限流大闸倍数
     */
    public final static Integer MULTIPLE_CROSS = 5;

    /**
     * 本地缓存时间
     * 单位：秒
     *
     */
    public final static Integer MEMORY_CACHE_TIME=216000;


    /**
     * 库存key前缀
     *
     */
    public final static String  PROMO_STOCK_PREFIX= "promo_item_stock_";


    /**
     * 大闸key前缀
     *
     */
    public final static String  PROMO_DOOR_PREFIX ="promo_door_count_";



    /**
     * 库存为0 key 前缀
     *
     */
    public final static String  PROMO_STOCK_INVALID_PREFIX ="promo_item_stock_invalid_";


    /**
     * 秒杀活动状态
     * 1 : 未开始
     * 2 : 正在进行中
     * 3 : 已经结束
     *
     */
    public final static Integer   PROMO_UNBEGIN=1;
    public final static Integer   PROMO_BEGINNING=2;
    public final static Integer   PROMO_END=3;


    /**
     * 库存日志状态
     * 1表示初始状态，2表示下单扣减库存成功，3表示下单回滚
     *
     */

    public final static Integer STOCK_LOG_STATE_INIT=1;
    public final static Integer STOCK_LOG_STATE_SUCCESS=2;
    public final static Integer STOCK_LOG_STATE_ROLLBACK=3;





    /**
     * 秒杀用的验证 redis key前缀
     *
     */
    public final static String  VERIFY_CODE_PREFIX=  "verify_code_";



    /**
     * 令牌桶数量
     *
     */
    public final static Integer RATELIMITER_COUNT=300;


    /**
     * mq 生产者组  消费者组  组名
     *
     */

    public final static String PRODUCER_GROUP_STOCK= "transaction_producer_group";

    public final static String CONSUMER_GROUP_STOCK="stock_consumer_group";


    /**
     * 秒杀令牌时间
     * 单位： 分钟
     */

    public final static Integer PROMO_TOKEN_REDIS_TIME=40;




}
