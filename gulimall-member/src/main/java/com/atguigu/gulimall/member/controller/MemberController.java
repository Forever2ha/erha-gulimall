package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atguigu.gulimall.member.vo.MemLoginVo;
import com.atguigu.gulimall.member.vo.RegisterMemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 会员
 *
 * @author erha
 * @email 1539280617@qq.com
 * @date 2021-12-28 22:56:41
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    //测试方法
    @RequestMapping("/coupons")
    public R coupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("假设是数据库查出来的李四");
        R r = couponFeignService.memberCoupons();

        return R.ok()
                .put("member",memberEntity)
                .put("coupons",r.get("coupons"));
    }



    @PostMapping("/register")
    public R register(@RequestBody RegisterMemVo registerMemVo){
        memberService.register(registerMemVo);
        return  R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemLoginVo vo){
        MemberEntity entity = memberService.login(vo);
        if (entity == null) return R.error("用户名或密码错误/未注册");
        return R.ok().setData(entity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
 //   @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
