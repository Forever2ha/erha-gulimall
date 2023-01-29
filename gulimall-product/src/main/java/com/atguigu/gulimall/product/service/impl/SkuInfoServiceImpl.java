package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SkuEsModel;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * key: '华为',//检索关键字
         * catelogId: 0,
         * brandId: 0,
         * min: 0,
         * max: 0
         */
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w->w.like("sku_id",key)).or().like("sku_name",key);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min) ){
            wrapper.ge("price",min);
        }
        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max) ){
            try {
                BigDecimal maxB = new BigDecimal(max);
                if ((maxB.compareTo(new BigDecimal("0")) == 1)){
                    wrapper.le("price",max);
                }
            } catch (Exception e){

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuEsModel> getSkusBySpuId(Long spuId) {
        SpuInfoEntity spuInfo = spuInfoService.getById(spuId);
        BrandEntity brandEntity = brandService.getById(spuInfo.getBrandId());
        CategoryEntity categoryEntity = categoryService.getById(spuInfo.getCatalogId());



        final List<SkuEsModel.Attrs> baseAttrs = new ArrayList<>();
        List<ProductAttrValueEntity> baseAttrList = attrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        if (baseAttrList != null){
            List<Long> attrIds = baseAttrList.stream()
                    .map(ProductAttrValueEntity::getAttrId)
                    .collect(Collectors.toList());
            List<Long> searchIds = attrService.selectSearchIds(attrIds);
            baseAttrs.addAll(baseAttrList.stream()
                    .map((attr) -> {
                        SkuEsModel.Attrs esAttr = new SkuEsModel.Attrs();
                        esAttr.setAttrId(attr.getAttrId());
                        esAttr.setAttrName(attr.getAttrName());
                        esAttr.setAttrValue(attr.getAttrValue());
                        return esAttr;
                    })
                    .filter((attr) -> searchIds.contains(attr.getAttrId()))
                    .collect(Collectors.toList()));
        }

        List<SkuInfoEntity> skuInfoEntityList = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        if (skuInfoEntityList.isEmpty())return null;

        R r = wareFeignService.skuHasStock(
                skuInfoEntityList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList())
        );
        Map<Long,Boolean> skuHasStockMap = new HashMap<>();
        for (SkuHasStockTo to : r.getData(new TypeReference<List<SkuHasStockTo>>() {
        })) {
            skuHasStockMap.put(to.getSkuId(),to.getHasStock());
        }
        return skuInfoEntityList.stream()
                .map((sku) -> {
                    SkuEsModel skuEsModel = new SkuEsModel();
                    BeanUtils.copyProperties(sku,skuEsModel);
                    skuEsModel.setSkuPrice(sku.getPrice());
                    skuEsModel.setSkuImg(sku.getSkuDefaultImg());
                    skuEsModel.setHasStock(skuHasStockMap.getOrDefault(sku.getSkuId(),false));

                    skuEsModel.setHotScore(1L);

                    skuEsModel.setBrandId(spuInfo.getBrandId());
                    skuEsModel.setBrandName(brandEntity.getName());
                    skuEsModel.setBrandImg(brandEntity.getLogo());
                    skuEsModel.setCatalogId(categoryEntity.getCatId());
                    skuEsModel.setCatalogName(categoryEntity.getName());

                    skuEsModel.setAttrs(baseAttrs);


                    return skuEsModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SkuItemVo item(Long skuId) {
        if (skuId == null) return null;
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1.获取基本信息 pms_sku_info
        CompletableFuture<Object> f1 = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }).thenApplyAsync((skuInfoEntity -> {
            // 1.5 查看是否有库存
            CompletableFuture<Void> f1_5 = CompletableFuture.supplyAsync(() -> {
                try {
                    R r = wareFeignService.skuHasStock(Collections.singletonList(skuInfoEntity.getSkuId()));
                    Boolean hasStock = r.getData(new TypeReference<List<SkuHasStockTo>>() {
                    }).get(0).getHasStock();
                    skuItemVo.setHasStock(hasStock);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    skuItemVo.setHasStock(false);
                }
                return null;
            });
            // 3.获取spu的销售属性组合
            CompletableFuture<Void> f3 = CompletableFuture.supplyAsync(() -> {
                List<SkuItemVo.SkuItemSaleAttrVo> list = attrService.list(skuInfoEntity.getSpuId());
                skuItemVo.setSaleAttr(list);
                return null;
            });

            // 4.获取spu介绍
            CompletableFuture<Void> f4 = CompletableFuture.supplyAsync(() -> {
                skuItemVo.setDesc(spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>()
                        .eq("spu_id",skuInfoEntity.getSpuId())));
                return null;
            });

            // 5.获取spu规格参数信息
            CompletableFuture<Void> f5 = CompletableFuture.supplyAsync(() -> {
                List<SkuItemVo.SpuItemAttrGroupVo> list = attrValueService.list(skuInfoEntity.getSpuId());
                skuItemVo.setGroupAttrs(list);
                return null;
            });

            CompletableFuture<Void> all = CompletableFuture.allOf(f3, f1_5, f5, f4);
            try {
                all.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return null;
        }));

        // 2.获取sku图片信息
        CompletableFuture<Void> f2 = CompletableFuture.supplyAsync(() -> {
            List<SkuImagesEntity> list = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(list);
            return null;
        });


        CompletableFuture<Void> allOf = CompletableFuture.allOf(f1,f2);
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return skuItemVo;
    }

}