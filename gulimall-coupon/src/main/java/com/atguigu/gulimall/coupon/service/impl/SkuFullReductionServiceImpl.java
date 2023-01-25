package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    SkuFullReductionService skuFullReductionService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //sku的优惠满减信息
        // sms_sku_ladder
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadder);
        skuLadder.setAddOther(skuReductionTo.getCountStatus());
        if (skuLadder.getFullCount() > 0){
            skuLadderService.save(skuLadder);
        }


        // sms_sku_full_reduction
        SkuFullReductionEntity fullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,fullReduction);
        if (fullReduction.getFullPrice().compareTo(new BigDecimal(0)) > 0){
            this.save(fullReduction);
        }


        // sms_member_price
        List<SkuReductionTo.MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        if (memberPrices != null && !memberPrices.isEmpty()){
            List<MemberPriceEntity> collect = memberPrices.stream()
                    .map(member -> {
                        MemberPriceEntity entity = new MemberPriceEntity();
                        entity.setSkuId(skuReductionTo.getSkuId())
                                .setMemberLevelId(member.getId())
                                .setMemberPrice(member.getPrice())
                                .setMemberLevelName(member.getName())
                                .setAddOther(1);
                        return entity;
                    })
                    .filter(memberPrice -> memberPrice.getMemberPrice().compareTo(new BigDecimal("0")) > 0)
                    .collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }

    }


}