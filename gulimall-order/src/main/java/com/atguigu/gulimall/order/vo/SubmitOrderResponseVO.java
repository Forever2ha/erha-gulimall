package com.atguigu.gulimall.order.vo;


import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 提交订单返回结果
 * @author: wan
 */
@Data
public class SubmitOrderResponseVO {
    private OrderEntity order;
    private Integer code;
}
