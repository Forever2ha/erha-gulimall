package com.atguigu.gulimall.member.service.impl;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.gulimall.member.vo.MemLoginVo;
import com.atguigu.gulimall.member.vo.RegisterMemVo;
import com.sun.deploy.net.HttpUtils;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.cloud.netflix.ribbon.apache.HttpClientUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(RegisterMemVo registerMemVo) {

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname(registerMemVo.getUserName());
        memberEntity.setLevelId(1L);
        if (getOne(new QueryWrapper<MemberEntity>().eq("username",registerMemVo.getUserName())) != null
        ||
                getOne(new QueryWrapper<MemberEntity>().eq("mobile",registerMemVo.getPhone())) != null
        ){
            throw new RuntimeException("手机号或用户名重复！");
        }
        // TODO: 2023/1/29 密码加密
        memberEntity.setPassword(registerMemVo.getPassword());
        memberEntity.setMobile(registerMemVo.getPhone());
        save(memberEntity);

    }

    @Override
    public MemberEntity login(MemLoginVo vo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.and((w) -> {
            w.eq("username",vo.getLoginacct())
                    .or()
                    .eq("mobile",vo.getLoginacct());
        });
        wrapper.eq("password",vo.getPassword());
        return getOne(wrapper);
    }

}