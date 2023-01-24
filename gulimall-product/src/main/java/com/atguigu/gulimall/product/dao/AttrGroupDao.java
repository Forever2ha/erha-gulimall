package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性分组
 * 
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:01
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities);
}
