package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SpuSaveInfoVo;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        /**
         *  key: '华为',//检索关键字
         *    catelogId: 6,//三级分类id
         *    brandId: 1,//品牌id
         *    status: 0,//商品状态
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                w.like("id",key).or().like("spu_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catelog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status) && !"0".equalsIgnoreCase(status)){
            if ("3".equalsIgnoreCase(status)){
                wrapper.eq("publish_status",0);
            }else{
                wrapper.eq("publish_status",status);
            }

        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveInfoVo vo) {

        // 1.保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        save(spuInfoEntity);

        // 2.保存spu描述图片 pms_spu_info_desc
        List<String> descript = vo.getDecript();
        if (descript.size() > 0){
            SpuInfoDescEntity entity = new SpuInfoDescEntity();
            entity.setSpuId(spuInfoEntity.getId());
            entity.setDecript(String.join(",",descript));
            spuInfoDescService.save(entity);
        }

        // 3.保存spu图片集 pms_spu_images
        spuImagesService.saveImages(spuInfoEntity.getId(), vo.getImages());

        // 4.保存spu规格参数；pms_product_attr_value
        List<SpuSaveInfoVo.BaseAttrs> baseAttrs = vo.getBaseAttrs();
        if (baseAttrs != null && baseAttrs.size() != 0){
            List<ProductAttrValueEntity> collect = baseAttrs.stream()
                    .map((baseAttr) -> {
                        ProductAttrValueEntity entity = new ProductAttrValueEntity();
                        entity.setSpuId(spuInfoEntity.getId());
                        entity.setAttrId(baseAttr.getAttrId());
                        entity.setAttrValue(baseAttr.getAttrValues());
                        entity.setQuickShow(baseAttr.getShowDesc());

                        AttrEntity attr = attrService.getById(baseAttr.getAttrId());
                        if (attr != null){
                            entity.setAttrName(attr.getAttrName());
                        }
                        return entity;
                    })
                    .collect(Collectors.toList());
            productAttrValueService.saveBatch(collect);
        }

        // 4.5 保存spu积分 sms_spu_bounds

        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(vo.getBounds(),spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());

        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (!r.getCode().equals(0)){
            throw new RuntimeException("[远程调用]保存bounds信息失败！");
        }

        // 5.保存spu的sku信息
        SkuInfoEntity skuInfo = new SkuInfoEntity();
        List<SpuSaveInfoVo.Skus> skus = vo.getSkus();
        if (skus != null && !skus.isEmpty()){
            skus.forEach(sku -> {
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setSpuId(spuInfoEntity.getId());
                skuInfo.setPrice(new BigDecimal(sku.getPrice()));
                skuInfo.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfo.setBrandId(spuInfoEntity.getBrandId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSkuDesc(sku.getSkuSubtitle());
                for (SpuSaveInfoVo.Images image : sku.getImages()) {
                    if (image.getDefaultImg().equals(1)){
                        skuInfo.setSkuDefaultImg(image.getImgUrl());
                        break;
                    }
                }
                // 5.1 sku基本信息 pms_sku_info
                skuInfoService.save(skuInfo);

                List<SkuImagesEntity> collect = sku.getImages()
                        .stream()
                        .map(img -> {
                            SkuImagesEntity imagesEntity = new SkuImagesEntity();
                            imagesEntity.setSkuId(skuInfo.getSkuId());
                            imagesEntity.setImgUrl(img.getImgUrl());
                            imagesEntity.setDefaultImg(img.getDefaultImg());
                            return imagesEntity;
                        })
                        .filter((img) -> !StringUtils.isEmpty(img.getImgUrl()))
                        .collect(Collectors.toList());

                // 5.2 sku图片信息 pms_sku_images
                skuImagesService.saveBatch(collect);

                List<SpuSaveInfoVo.Attr> attrList = sku.getAttr();
                if (attrList != null && !attrList.isEmpty()){
                    List<SkuSaleAttrValueEntity> collect1 = attrList.stream()
                            .map((attr) -> {
                                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                                BeanUtils.copyProperties(attr,entity);
                                entity.setSkuId(skuInfo.getSkuId());
                                return entity;
                            })
                            .collect(Collectors.toList());
                    // 5.3 sku的销售属性信息 pms_sku_sale_attr_value
                    skuSaleAttrValueService.saveBatch(collect1);
                }

                // 5.4 sku的优惠满减信息 sms_sku_ladder sms_sku_full_reduction sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuInfo.getSkuId());

                if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R res = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (!res.getCode().equals(0)){
                        throw new RuntimeException("[远程调用]保存优惠满减信息失败！");
                    }
                }
            });
        }


    }

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void upSpu(Long spuId) {

    }

}