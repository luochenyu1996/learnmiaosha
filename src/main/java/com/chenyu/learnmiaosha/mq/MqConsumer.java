package com.chenyu.learnmiaosha.mq;


import com.alibaba.fastjson.JSON;
import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.mapper.ItemStockDOMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 *
 * mq 的消费者
 *
 */
@Component
public class MqConsumer {

    private DefaultMQPushConsumer consumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;

    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer(Constant.CONSUMER_GROUP_STOCK);
        consumer.setNamesrvAddr(nameAddr);
        consumer.subscribe(topicName,"*");

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            //实现库存真正到数据库内扣减的逻辑
            Message msg = msgs.get(0);
            String jsonString  = new String(msg.getBody());
            Map<String,Object>map = JSON.parseObject(jsonString, Map.class);
            Integer itemId = (Integer) map.get("itemId");
            Integer amount = (Integer) map.get("amount");
            //收到消息后对数据库进行扣减
            itemStockDOMapper.decreaseStock(itemId,amount);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

    }
}
