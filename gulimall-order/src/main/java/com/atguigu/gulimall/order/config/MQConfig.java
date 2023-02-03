package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {


    @Autowired
    AmqpAdmin amqpAdmin;

    public Exchange orderEventExchange(){
        // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange",true,false,null);
    }


    public Queue orderDelayQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete,
        //			@Nullable Map<String, Object> arguments

        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","order-event-exchange");
        args.put("x-dead-letter-routing-key","order.release.order");
        args.put("x-message-ttl",60000);
        return new Queue("order.delay.queue",
                    true,
                false,
                false,
                args
                );
    }


    public Queue orderReleaseQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete,
        //			@Nullable Map<String, Object> arguments

        return new Queue("order.release.queue",
                true,
                false,
                false,
               null
        );
    }


    public Binding orderDelayBinding(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments
        return new Binding(
                "order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null
        );
    }


    public Binding orderReleaseBinding(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments
        return new Binding(
                "order.release.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null
        );
    }



    //创建交换机和对列
    @Bean
    public void createExchangeQueue (){
       amqpAdmin.declareExchange(orderEventExchange());
       amqpAdmin.declareQueue(orderDelayQueue());
       amqpAdmin.declareQueue(orderReleaseQueue());
       amqpAdmin.declareBinding(orderDelayBinding());
       amqpAdmin.declareBinding(orderReleaseBinding());
    }

}
