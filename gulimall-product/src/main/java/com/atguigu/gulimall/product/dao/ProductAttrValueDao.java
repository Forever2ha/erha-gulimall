package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:00
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> selectSpuItemAttrGroupVoList(@Param("spuId") Long spuId);
}
