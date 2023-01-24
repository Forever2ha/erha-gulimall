package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.vo.SpuSaveInfoVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:02:57
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveInfoVo spuSaveInfoVo);

    void upSpu(Long spuId);
}

