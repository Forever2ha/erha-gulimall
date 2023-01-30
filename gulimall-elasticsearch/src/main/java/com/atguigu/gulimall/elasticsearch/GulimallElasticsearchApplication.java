package com.atguigu.gulimall.elasticsearch;

import com.atguigu.common.SpringSessionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.elasticsearch.feign")
@Import(SpringSessionConfig.class)
public class GulimallElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallElasticsearchApplication.class, args);
    }

}
