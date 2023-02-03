package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @GetMapping("ware/waresku/lock")
    R testLock();

}
