package com.atguigu.gulimall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //获取key查询关键字
        String key = (String) params.get("key");

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();




        if (!StringUtils.isEmpty(key)){
            wrapper.like("brand_id",key).or()
                    .like("name",key).or()
                    .like("first_letter",key);

        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }



    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
       this.updateById(brand);
        if (!StringUtils.isEmpty(brand.getName())){
            categoryBrandRelationService.updateBrandName(brand.getBrandId(),brand.getName());

            // TODO 其它关于brand的冗余字段更新
        }



    }

}