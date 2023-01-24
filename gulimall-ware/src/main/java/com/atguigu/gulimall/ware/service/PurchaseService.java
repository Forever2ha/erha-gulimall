package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.exception.WareException;
import com.atguigu.gulimall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 23:10:41
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils getUnreceiveList();

    void merge(MergeVo mergeVo) throws WareException;

    void receive(Long[] pdids);
}

