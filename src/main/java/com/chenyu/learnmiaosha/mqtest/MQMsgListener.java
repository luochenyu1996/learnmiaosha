package com.chenyu.learnmiaosha.mqtest;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chen yu
 * @create 2021-12-06 22:48
 */
@Component
public class MQMsgListener implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println("监听成功");

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
