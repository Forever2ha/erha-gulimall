package com.atguigu.gulimall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationServiceImpl attrAttrgroupRelationService;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveBaseAttr(AttrVo attr) {
        //1.保存attr的基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);

        attr.setAttrId(attrEntity.getAttrId());
        //2.保存attr与attrgroup的关联信息
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        BeanUtils.copyProperties(attr,attrAttrgroupRelationEntity);

        attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
    }

    @Override
    public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId) {

        return null;
    }

    @Transactional
    @Override
    public AttrRespVo getDetail(Long attrId) {


        return null;
    }

    @Transactional
    @Override
    public void updateDetail(AttrVo attrvo) {

    }

    @Override
    public PageUtils querySaleAttrList(Map<String, Object> params, Long catelogId) {
        return null;
    }

    @Override
    public void saveSaleAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.save(attrEntity);
    }

    @Transactional
    @Override
    public PageUtils getNoattrRelation(Long attrgroupId, Map<String, Object> params) {
       return null;

    }

    @Override
    public List<Long> selectSearchIds(List<Long> allAttrIds) {
        return baseMapper.selectSearchIds(allAttrIds);
    }

}