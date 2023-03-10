package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.product.CategoryConstant;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import org.apache.commons.lang.StringUtils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate redisTemplate;



    @Autowired
    RedissonClient redissonClient;

    public static List<CategoryEntity> getSonMenu(List<CategoryEntity> all,Long id){
        return all.stream().filter(i-> Objects.equals(i.getParentCid(), id)).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJson() {
        // 1. ?????????
        Map<String,List<Catalog2Vo>> cache = getFromCache();
        if (cache != null){
            return cache;
        }

        RLock lock = redissonClient.getLock("catalog-lock");
        lock.lock();
        Map<String,List<Catalog2Vo>> result = null;
        try {
            Map<String, List<Catalog2Vo>> cacheNd = getFromCache();
            if (cacheNd == null){
                log.error("?????????????????????????????????????????????????????????????????????????????????");
                result = getFromDb();
                // ????????????
                redisTemplate.opsForValue().set("catalogJSON",JSON.toJSONString(result),10,TimeUnit.MINUTES);
            }else {
                return cacheNd;
            }

        }finally {
            lock.unlock();
        }

        return result;
    }

    private Map<String, List<Catalog2Vo>> getFromCache() {

        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isBlank(catalogJSON)){
            return JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
        }
        return null;
    }

    private Map<String, List<Catalog2Vo>> getFromDb() {
        // 2.???????????????????????????parentCid??????
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.??????1?????????
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.????????????
        return level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 5.??????2????????????????????????List<Catalog2Vo>
            List<Catalog2Vo> catalog2VoS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 7.??????3????????????????????????List<Catalog3VO>
                        List<Catalog2Vo.Catalog3Vo> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // ??????3?????????VO
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        // ??????2?????????VO??????
                        Catalog2Vo catalog2Vo = new Catalog2Vo(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2Vo;
                    }).collect(Collectors.toList());
            return catalog2VoS;
        }));
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );


        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //??????????????????
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //??????????????????
        List<CategoryEntity> menu1Level = entities.stream()
                .filter((categoryEntity -> categoryEntity.getParentCid() == 0))
                .map((menu)->{
                    //???????????????????????????
                    menu.setChildren(getChildren(menu,entities));
                    return menu;
                })
                .sorted((menu1,menu2)-> menu1.getSort()-menu2.getSort())
                .collect(Collectors.toList());


        return menu1Level;
    }

    @Override
    public void removeMenus(List<Long> catIds) {

        //TODO ????????????????????????????????????
        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] getCatelogPath(Long catelogId) {
        ArrayList<Long> path = new ArrayList<>();
        getCategoryPath(path,catelogId);

        Collections.reverse(path);

        return path.toArray(new Long[0]);
    }

    @CacheEvict(cacheNames = CategoryConstant.CACHE_KEY_CATALOG_JSON,allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategoryName(category.getCatId(),category.getName());
        }

    }

    @Override
    public String getCatelogNamesPath(Long catelogId) {
        StringBuilder namesPath = new StringBuilder();
        Long[] catelogPath = getCatelogPath(catelogId);
        for (Long catId : catelogPath) {
            CategoryEntity categoryEntity = baseMapper.selectById(catId);
            namesPath.append(categoryEntity.getName()+'/');
        }
        namesPath.deleteCharAt(namesPath.length()-1);

        return namesPath.toString();

    }

    @Override
    public String getCatelogNamesPath(Long[] catelogPath) {
        StringBuilder namesPath = new StringBuilder();
        for (Long catId : catelogPath) {
            CategoryEntity categoryEntity = baseMapper.selectById(catId);
            namesPath.append(categoryEntity.getName()+'/');
        }
        namesPath.deleteCharAt(namesPath.length()-1);

        return namesPath.toString();


    }

    @Override
    @Cacheable(value = CategoryConstant.CACHE_KEY_CATALOG_JSON,key = "#root.methodName")
    public List<CategoryEntity> getLevel1Category() {
        log.error("????????????????????????????????????");
        return baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid",0)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(cacheNames = CategoryConstant.CACHE_KEY_CATALOG_JSON,allEntries = true)
    public void updateCascadeSort(List<CategoryEntity> categoryEntityList) {
        // 1. ??????category
        this.updateBatchById(categoryEntityList);

    }

    private void getCategoryPath(ArrayList<Long> path, Long catelogId) {
        path.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            getCategoryPath(path,category.getParentCid());
        }

    }

    private List<CategoryEntity> getChildren(CategoryEntity menu, List<CategoryEntity> all) {
        //???menu????????????
        List<CategoryEntity> childrenList = all.stream()
                .filter((categoryEntity -> menu.getCatId().longValue() == categoryEntity.getParentCid().longValue()))
                .map(children->{
                    children.setChildren(getChildren(children,all));
                    return children;
                })
                .sorted((menu1, menu2) -> menu.getSort() - menu2.getSort())
                .collect(Collectors.toList());

        return childrenList;

    }

}