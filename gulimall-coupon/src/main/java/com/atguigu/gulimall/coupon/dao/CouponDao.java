package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author erha
 * @email 1539280617@qq.com
 * @date 2022-01-05 12:06:30
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
