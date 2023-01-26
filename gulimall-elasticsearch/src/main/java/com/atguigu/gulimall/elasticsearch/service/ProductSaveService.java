package com.atguigu.gulimall.elasticsearch.service;



import com.atguigu.common.to.SkuEsModel;

import java.util.List;

public interface ProductSaveService {
    void saveSkus(List<SkuEsModel> skuEsModelList);
}
