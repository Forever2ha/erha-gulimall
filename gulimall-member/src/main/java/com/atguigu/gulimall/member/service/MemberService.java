package com.atguigu.gulimall.member.service;

import com.atguigu.gulimall.member.vo.MemLoginVo;
import com.atguigu.gulimall.member.vo.RegisterMemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 22:56:41
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(RegisterMemVo registerMemVo);

    MemberEntity login(MemLoginVo vo);
}

