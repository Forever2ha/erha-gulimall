package com.atguigu.common.vo.cart;


import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

// 购物车
public class Cart {

    private List<CartItem> items;
    private Integer countNum; // 商品数量
    private Integer countType; // 商品类型数量
    private BigDecimal totalAmount; // 商品总价
    private BigDecimal reduce = new BigDecimal("0"); // 减免价格


    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public Integer getCountNum() {
        int res = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                res += item.getCount();
            }
        }
        return res;
    }

    public Integer getCountType() {
        int res = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                res ++;
            }
        }
        return res;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal res = new BigDecimal("0");
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                res =res.add(new BigDecimal(item.getTotalPrice()+""));
            }
        }
        return res.subtract(getReduce());
    }

    public BigDecimal getReduce() {
        return reduce;
    }
}
