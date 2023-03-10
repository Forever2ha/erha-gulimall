package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.SkuHasStockTo;
import com.baomidou.mybatisplus.extension.service.IService;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * εεεΊε­
 *
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 23:10:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {



    PageUtils queryPage(Map<String, Object> params);


    List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds);

    void lockSku46();

}

