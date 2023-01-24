
package com.atguigu.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class SpuSaveInfoVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private Integer publishStatus;

    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


    @Data
    public static class Bounds {
        private BigDecimal buyBounds;
        private BigDecimal growBounds;
    }

    @Data
    public static class BaseAttrs {
        private Long attrId;
        private String attrValues;
        private Integer showDesc;
    }

    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
    @Data
    public static class Images {
        private String imgUrl;
        private Integer defaultImg;
    }
    @Data
    public static class MemberPrice {
        private Long id;
        private String name;
        private BigDecimal price;
    }
    @Data
    public static class Skus {
        private List<Attr> attr;
        private String skuName;
        private String price;
        private String skuTitle;
        private String skuSubtitle;
        private List<Images> images;
        private List<String> descar;
        private Integer fullCount;
        private BigDecimal discount;
        private Integer countStatus;
        private BigDecimal fullPrice;
        private BigDecimal reducePrice;
        private Integer priceStatus;
        private List<MemberPrice> memberPrice;
    }
}