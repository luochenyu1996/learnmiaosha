package com.chenyu.learnmiaosha;

import com.chenyu.learnmiaosha.mq.MqProducer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LearnmiaoshaApplicationTests {






    @Resource
   MqProducer mqProducer;

    @Test
    void contextLoads() {

    }



    @Test
    void testSendRocketMq() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {


        mqProducer.transactionAsyncReduceStock(23,6,1,1,"sssss");

        Thread.sleep(10000);

    }






}
