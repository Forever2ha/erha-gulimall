package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo implements Serializable {

    private String catalog1Id;  // 1级父分类ID
    private List<Catalog3Vo> catalog3List;// 3级子分类集合
    private String id;  // 2级分类ID
    private String name;  // 2级分类name

    /**
     * 三级分类Vo
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catalog3Vo implements Serializable {
        private String catalog2Id;  // 2级父分类ID
        private String id;  // 3级分类ID
        private String name;  // 3级分类name
    }
}

