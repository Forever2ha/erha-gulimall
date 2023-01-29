package com.atguigu.gulimall.thirdparty.controller;


import com.atguigu.common.constant.auth.AuthConstant;
import com.atguigu.common.constant.member.MemberConstant;
import com.atguigu.common.utils.R;
import com.sun.xml.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class SmsController {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code")String code){
        // TODO: 2023/1/29 发送验证码
        log.info("发送验证码：{}:{}",phone,code);
        stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX+ phone,
                code
                ,60, TimeUnit.SECONDS);
        return R.ok();
    }
}
