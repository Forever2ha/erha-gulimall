package com.atguigu.gulimall.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.search.EsConstant;
import com.atguigu.common.to.SkuEsModel;
import com.atguigu.gulimall.elasticsearch.config.ElasticConfig;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atguigu.gulimall.elasticsearch.service.ProductSaveService;

import java.io.IOException;
import java.util.List;

@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient client;

    @Override
    public void saveSkus(List<SkuEsModel> skuEsModelList) {
        BulkRequest bulkRequest = new BulkRequest();
        skuEsModelList.forEach(skuEsModel -> {
            //1.create IndexRequest
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //2.add Json str and id to indexRequest.source
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            indexRequest.id(String.valueOf(skuEsModel.getSkuId()));
            bulkRequest.add(indexRequest);
        });



        //3.execute
        try {
            BulkResponse bulk = client.bulk(bulkRequest, ElasticConfig.COMMON_OPTIONS);
            System.out.println(bulk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
