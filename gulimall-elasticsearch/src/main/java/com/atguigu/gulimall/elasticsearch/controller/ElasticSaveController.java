package com.atguigu.gulimall.elasticsearch.controller;

import com.atguigu.common.to.SkuEsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.elasticsearch.service.ProductSaveService;

import java.util.List;

@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R save(@RequestBody List<SkuEsModel> skuEsModelList){
        productSaveService.saveSkus(skuEsModelList);
        return R.ok();
    }
}
