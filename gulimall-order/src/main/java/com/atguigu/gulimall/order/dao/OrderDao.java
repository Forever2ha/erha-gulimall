package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 23:03:06
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
