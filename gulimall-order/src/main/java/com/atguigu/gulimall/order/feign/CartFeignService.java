package com.atguigu.gulimall.order.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("orderItem/{userId}")
    public R getOrderItem(@PathVariable("userId") Long userId);

}
