package com.atguigu.gulimall.product.feign;


import com.atguigu.common.to.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-elasticsearch")
public interface ElasticSearchFeignService {

    @PostMapping("/search/save/product")
    R save(@RequestBody List<SkuEsModel> skuEsModelList);
}
