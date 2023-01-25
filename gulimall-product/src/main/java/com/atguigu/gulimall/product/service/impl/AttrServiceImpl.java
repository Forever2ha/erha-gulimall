package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.product.AttrConstant;
import com.atguigu.gulimall.product.vo.SpuSaveInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(params);
        String key = String.valueOf(params.get("key"));
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!catelogId.equals(0L)){
            wrapper.eq("catelog_id",catelogId);
        }
        if (!StringUtils.isBlank(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key)
                        .or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> attrEntityIPage = baseMapper.selectPage(page, wrapper);

        List<AttrRespVo> collect = attrEntityIPage.getRecords().stream()
                .map((attrEntity -> {
                    AttrRespVo attrRespVo = new AttrRespVo();
                    BeanUtils.copyProperties(attrEntity,attrRespVo);
                    /**
                     * "catelogName": "手机/数码/手机", //所属分类名字
                     * 			"groupName": "主体", //所属分组名字
                     */
                    attrRespVo.setCatelogPath(categoryService.getCatelogPath(attrEntity.getCatelogId()));
                    attrRespVo.setCatelogName(categoryService.getCatelogNamesPath(attrEntity.getCatelogId()));
                    AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrRespVo.getAttrId())
                    );
                    if (relation != null){
                        Long groupId = relation.getAttrGroupId();
                        attrRespVo.setGroupName(
                                Optional.ofNullable(attrGroupDao.selectById(groupId))
                                        .orElseGet(()->new AttrGroupEntity().setAttrGroupName(""))
                                        .getAttrGroupName()
                        );
                    }


                    return attrRespVo;
                })).collect(Collectors.toList());


        return new PageUtils(collect,
                ((int) attrEntityIPage.getTotal()),
                (int) attrEntityIPage.getSize(),
                (int) attrEntityIPage.getCurrent());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AttrRespVo getDetail(Long attrId) {
        /**
         * "attrGroupId": 1, //分组id
         * 		"catelogPath": [2, 34, 225] //分类完整路径
         */
        AttrEntity attrEntity = this.getOne(new QueryWrapper<AttrEntity>().eq("attr_id", attrId));
        AttrRespVo res = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity,res);
        if (res.getAttrId() != null){
            res.setAttrGroupId(
                    Optional.ofNullable(attrAttrgroupRelationService.getOne(
                            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",res.getAttrId())
                    )).orElse(new AttrAttrgroupRelationEntity())
                            .getAttrGroupId()
            );
        }
        res.setCatelogPath(categoryService.getCatelogPath(res.getCatelogId()));
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDetail(AttrVo attrvo) {
        // 1.修改基础字段
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrvo,attrEntity);
        this.updateById(attrEntity);
        // 2. 修改其他表:
        //   "attrGroupId": 0, //属性分组id
        if (attrvo.getAttrType().equals(AttrConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) && attrvo.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(attrvo.getAttrId());
            relation.setAttrGroupId(attrvo.getAttrGroupId());
            attrAttrgroupRelationService.saveOrUpdate(relation,new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrvo.getAttrId()));
        }
    }

    @Override
    public PageUtils querySaleAttrList(Map<String, Object> params, Long catelogId) {
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(params);
        Object key = params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", AttrConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (!catelogId.equals(0L)){
            wrapper.eq("catelog_id",catelogId);
        }
        if (key != null){
            wrapper.and((w)->{
                w.eq("attr_id",key)
                        .or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> attrEntityIPage = baseMapper.selectPage(page, wrapper);

        List<AttrRespVo> collect = attrEntityIPage.getRecords().stream()
                .map((attrEntity -> {
                    AttrRespVo attrRespVo = new AttrRespVo();
                    BeanUtils.copyProperties(attrEntity,attrRespVo);
                    /**
                     * "catelogName": "手机/数码/手机", //所属分类名字
                     */
                    attrRespVo.setCatelogPath(categoryService.getCatelogPath(attrEntity.getCatelogId()));
                    attrRespVo.setCatelogName(categoryService.getCatelogNamesPath(attrEntity.getCatelogId()));
                    AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrRespVo.getAttrId())
                    );
                    return attrRespVo;
                })).collect(Collectors.toList());


        return new PageUtils(collect,
                ((int) attrEntityIPage.getTotal()),
                (int) attrEntityIPage.getSize(),
                (int) attrEntityIPage.getCurrent());
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
        IPage<AttrEntity> iPage = baseMapper.selectNoAttrRelation(attrgroupId,new Query<AttrEntity>().getPage(params));
        return new PageUtils(iPage);
    }

    @Override
    public List<Long> selectSearchIds(List<Long> allAttrIds) {
        return baseMapper.selectSearchIds(allAttrIds);
    }

}