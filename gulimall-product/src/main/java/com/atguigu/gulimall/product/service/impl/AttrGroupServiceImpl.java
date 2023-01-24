package com.atguigu.gulimall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;


import javax.validation.constraints.NotNull;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrServiceImpl attrService;
    @Autowired
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //提取pageParams
        IPage<AttrGroupEntity> pageInfo = new Query<AttrGroupEntity>().getPage(params);
        //结果
        PageUtils res = null;

        String key = (String) params.get("key");

        if (catelogId == 0 && StringUtils.isEmpty(key)){

            //查全部
            IPage<AttrGroupEntity> page = this.page(pageInfo,
                    new QueryWrapper<>()
            );
            //PageUtils将查询到的page结果进行包装
            res =  new PageUtils(page);

        }else if (catelogId == 0 && !StringUtils.isEmpty(key)){
            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_group_id",key).or()
                    .like("attr_group_name",key);
            IPage<AttrGroupEntity> page = this.page(pageInfo, queryWrapper);
            res = new PageUtils(page);
        }

        else {

            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catelog_id",catelogId);

            if (!StringUtils.isEmpty(key)){
                queryWrapper.and((wrapper)->{
                    wrapper.eq("attr_group_id",key).or()
                            .like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(pageInfo, queryWrapper);



            res = new PageUtils(page);
        }
        return  res;


    }

    @Override
    public List<AttrEntity> getRelatedAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));



        if (attrAttrgroupRelationEntities.size() != 0){

            List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(
                            (AttrAttrgroupRelationEntity::getAttrId))
                    .collect(Collectors.toList());
            List<AttrEntity> attrEntities = attrService.listByIds(attrIds);

            return attrEntities;
        }

        return new ArrayList<>();


    }

    @Override
    public void removeRelation(AttrGroupRelationVo[] attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = Arrays.asList(attrGroupRelationVos).stream()
                .map((attrGroupRelationVo -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(attrGroupRelationVo, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                }))
                .collect(Collectors.toList());
        attrGroupDao.deleteBatchRelation(attrAttrgroupRelationEntities);
    }

    @Override
    public List<AttrGroupEntity> getCatelogAndWithItsAttrs(@NotNull Long catelogId) {


        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        if (attrGroupEntities.size() == 0) return attrGroupEntities;

        List<AttrGroupEntity> res = attrGroupEntities.stream()
                .map(attrGroupEntity -> {
                    //1.查关联的attrId
                    List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationService.list(
                            new QueryWrapper<AttrAttrgroupRelationEntity>()
                                    .eq("attr_group_id",attrGroupEntity.getAttrGroupId())
                    );
                    if (relations.size() == 0) return attrGroupEntity;

                    List<Long> ids = relations.stream()
                            .map(AttrAttrgroupRelationEntity::getAttrId)
                            .collect(Collectors.toList());
                    //2.查相关的attr信息
                    List<AttrEntity> attrs = attrService.listByIds(ids);
                    attrGroupEntity.setAttrs(attrs);

                    return attrGroupEntity;
                })
                .collect(Collectors.toList());

        return res;

    }


}