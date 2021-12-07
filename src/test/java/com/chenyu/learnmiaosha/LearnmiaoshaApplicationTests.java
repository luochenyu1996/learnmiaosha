package com.chenyu.learnmiaosha;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LearnmiaoshaApplicationTests {

    @Resource
    DefaultMQProducer defaultMQProducer;

    @Resource
    DefaultMQPushConsumer  defaultMQPushConsumer;

    @Test
    void contextLoads() {
    }



    @Test
    void testSendRocketMq() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        String msg = "rocketmq发送查询消息：查询成功";

        for (int i = 0; i < 10; i++) {
            Message sendMsg = new Message("MyTopic", "MyTag", msg.getBytes());
            //默认3秒超时
            SendResult sendResult = defaultMQProducer.send(sendMsg);
            SendStatus sendStatus = sendResult.getSendStatus();
            System.out.println("消息发送响应信息状态：" + sendStatus+"条数："+i);
            System.out.println("消息发送响应信息：" + sendResult.toString());

        }






    }






}
