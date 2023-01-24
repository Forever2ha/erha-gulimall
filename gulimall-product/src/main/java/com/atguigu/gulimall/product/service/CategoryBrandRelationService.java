package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.vo.BrandRespVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:00
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrandName(Long brandId, String name);

    void updateCategoryName(Long catId, String name);

    List<BrandRespVo> getCatelogRelatedBrand(Long catId);
}

