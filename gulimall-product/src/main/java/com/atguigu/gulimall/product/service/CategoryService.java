package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:00
 */
public interface CategoryService extends IService<CategoryEntity> {

     Map<String, List<Catalog2Vo>> getCatelogJson();

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenus(List<Long> asList);

    Long[] getCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    String getCatelogNamesPath(Long catelogId);

    String getCatelogNamesPath(Long[] catelogPath);

    List<CategoryEntity> getLevel1Category();

    void updateCascadeSort(List<CategoryEntity> categoryEntityList);
}

