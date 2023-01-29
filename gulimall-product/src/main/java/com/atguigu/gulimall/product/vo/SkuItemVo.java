package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuItemVo {

    //1、sku基本信息（pms_sku_info）【默认图片、标题、副标题、价格】
    private SkuInfoEntity info;

    private boolean hasStock;// 是否有货

    //2、sku图片信息（pms_sku_images）
    private List<SkuImagesEntity> images;

    //3、当前sku所属spu下的所有销售属性组合（pms_sku_sale_attr_value）
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、spu商品介绍（pms_spu_info_desc）【描述图片】
    private SpuInfoDescEntity desc;

    //5、spu规格参数信息（pms_attr）【以组为单位】
    private List<SpuItemAttrGroupVo> groupAttrs;

    //6、秒杀商品的优惠信息
    private SeckillSkuVo seckillSku;

    @Data
    public static class SkuItemSaleAttrVo {
        /**
         * 1.销售属性对应1个attrName
         * 2.销售属性对应n个attrValue
         * 3.n个sku包含当前销售属性（所以前端根据skuId交集区分销售属性的组合【笛卡尔积】）
         */
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }
    @Data
    public static class AttrValueWithSkuIdVo {
        private String attrValue;
        private String skuIds;
    }

    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuSaveInfoVo.Attr> attrs;
    }

    @Data
    public static class SeckillSkuVo {

        /**
         * 活动id
         */
        private Long promotionId;
        /**
         * 活动场次id
         */
        private Long promotionSessionId;
        /**
         * 商品id
         */
        private Long skuId;
        /**
         * 秒杀价格
         */
        private BigDecimal seckillPrice;
        /**
         * 秒杀总量
         */
        private Integer seckillCount;
        /**
         * 每人限购数量
         */
        private Integer seckillLimit;
        /**
         * 排序
         */
        private Integer seckillSort;

        //当前商品秒杀的开始时间
        private Long startTime;

        //当前商品秒杀的结束时间
        private Long endTime;

        //当前商品秒杀的随机码
        private String randomCode;

    }


}
