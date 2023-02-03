package com.atguigu.gulimall.ware.dao;

import com.atguigu.common.to.SkuHasStockTo;
import org.apache.ibatis.annotations.Param;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品库存
 * 
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 23:10:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Integer hasStock(@Param("skuId") Long skuId);

    List<SkuHasStockTo> getHasStock(@Param("skuIds") List<Long> skuIds);

    void lock(int i);
}
