package com.atguigu.gulimall.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.elasticsearch.feign")
public class GulimallElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallElasticsearchApplication.class, args);
    }

}
