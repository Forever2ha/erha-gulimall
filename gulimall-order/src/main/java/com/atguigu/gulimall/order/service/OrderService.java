package com.atguigu.gulimall.order.service;

import com.atguigu.common.vo.order.OrderConfirmVO;
import com.atguigu.common.vo.order.OrderSubmitVO;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 23:03:06
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVO confirmOrder();

    SubmitOrderResponseVO submit(OrderSubmitVO vo);
}

