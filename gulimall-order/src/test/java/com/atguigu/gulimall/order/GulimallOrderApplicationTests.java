package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class GulimallOrderApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void send(){
        OrderEntity orderEntity = new OrderEntity();
        rabbitTemplate.convertAndSend("exchange.direct","atguigu.news",orderEntity);
    }

    @Test
    void get(){
        OrderEntity orderEntity = (OrderEntity) rabbitTemplate.receiveAndConvert("atguigu.news");
        System.out.println(orderEntity);
    }

    @Test
    void createExchange(){
        amqpAdmin.declareExchange(new DirectExchange("hello_java_exchange",
                true,
                false
                ));

    }

}
