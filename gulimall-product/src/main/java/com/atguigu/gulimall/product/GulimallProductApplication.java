package com.atguigu.gulimall.product;


import com.atguigu.common.SpringSessionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;


@EnableCaching
@EnableDiscoveryClient//开启服务注册功能
//@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@Import(SpringSessionConfig.class)
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
