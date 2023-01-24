package com.atguigu.gulimall.member.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import com.atguigu.common.utils.R;


@FeignClient("gulimall-coupon") //声明式远程调用，value为远程调用的服务名字
public interface CouponFeignService {

    @RequestMapping("coupon/coupon/member/list")
    public R memberCoupons();


}
