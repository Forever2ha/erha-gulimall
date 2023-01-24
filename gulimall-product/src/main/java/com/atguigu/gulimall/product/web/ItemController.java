package com.atguigu.gulimall.product.web;

import org.springframework.web.bind.annotation.GetMapping;

public class ItemController {

    @GetMapping("/{skuId}.html")
    public String item(){

        return "item";
    }
}
