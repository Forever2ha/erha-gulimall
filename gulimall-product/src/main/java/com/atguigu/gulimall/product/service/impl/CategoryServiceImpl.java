package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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


    public static List<CategoryEntity> getSonMenu(List<CategoryEntity> all,Long id){
        return all.stream().filter(i-> Objects.equals(i.getParentCid(), id)).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJson() {

        return null;
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
        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //找到一级分类
        List<CategoryEntity> menu1Level = entities.stream()
                .filter((categoryEntity -> categoryEntity.getParentCid() == 0))
                .map((menu)->{
                    //找一级菜单的子菜单
                    menu.setChildren(getChildren(menu,entities));
                    return menu;
                })
                .sorted((menu1,menu2)-> menu1.getSort()-menu2.getSort())
                .collect(Collectors.toList());


        return menu1Level;
    }

    @Override
    public void removeMenus(List<Long> catIds) {

        //TODO 检测当前分类是否有子分类
        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] getCatelogPath(Long catelogId) {
        ArrayList<Long> path = new ArrayList<>();
        getCategoryPath(path,catelogId);



        Collections.reverse(path);

        return path.toArray(new Long[0]);
    }

@CacheEvict(value = "category",key = "'43  '")
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
    public List<CategoryEntity> getLevel1Category() {
        return baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid",0)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascadeSort(List<CategoryEntity> categoryEntityList) {
        // 1. 修改category
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
        //找menu的子菜单
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