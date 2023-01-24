package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌分类关联
 * 
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:00
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCategoryName(@Param("catId") Long catId, @Param("name") String name);
}
