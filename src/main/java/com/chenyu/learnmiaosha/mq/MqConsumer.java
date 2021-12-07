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
        consumer.subscribe(topicName,"increase");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            //实现库存真正到数据库内扣减的逻辑
            Message msg = msgs.get(0);
            System.out.println("消息接受成功");
            System.out.println("topic:"+msg.getTopic());
            String jsonString  = new String(msg.getBody());
            System.out.println(jsonString);
            Map<String,Object>map = JSON.parseObject(jsonString, Map.class);
            Integer itemId = (Integer) map.get("itemId");
            Integer amount = (Integer) map.get("amount");
            itemStockDOMapper.decreaseStock(itemId,amount);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
