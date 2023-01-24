package com.atguigu.gulimall.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.BrandRespVo;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private BrandDao brandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelation.setBrandName(brandDao.selectById(categoryBrandRelation.getBrandId()).getName());
        categoryBrandRelation.setCatelogName(categoryDao.selectById(categoryBrandRelation.getCatelogId()).getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrandName(Long brandId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandName(name);
        entity.setId(brandId);

        this.update(entity,new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategoryName(Long catId, String name) {
        baseMapper.updateCategoryName(catId,name);

    }

    @Override
    public List<BrandRespVo> getCatelogRelatedBrand(Long catId) {

        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper
                .selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        List<BrandRespVo> brandRespVos = categoryBrandRelationEntities.stream()
                .map(item -> {
                    BrandRespVo brandRespVo = new BrandRespVo();
                    BeanUtils.copyProperties(item, brandRespVo);
                    return brandRespVo;
                })
                .collect(Collectors.toList());

        return brandRespVos;

    }

}