package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:01
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBaseAttr(AttrVo attr);

    PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId);

    AttrRespVo getDetail(Long attrId);

    void updateDetail(AttrVo attrvo);

    PageUtils querySaleAttrList(Map<String, Object> params, Long catelogId);

    void saveSaleAttr(AttrVo attrVo);

    PageUtils getNoattrRelation(Long attrgroupId, Map<String, Object> params);

    List<Long> selectSearchIds(List<Long> allAttrIds);

    List<SkuItemVo.SkuItemSaleAttrVo> list(Long spuId);
}

