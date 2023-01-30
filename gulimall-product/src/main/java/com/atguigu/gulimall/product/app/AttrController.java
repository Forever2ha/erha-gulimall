package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;


/**
 * 商品属性
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 20:44:31
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {


    @Autowired
    private AttrService attrService;



    @GetMapping("/stringList/{skuId}")
    public R getSkuSaleAttrStringList(@PathVariable Long skuId){
        List<String> res = attrService.listSaleAttrStringList(skuId);
        return R.ok().setData(res);
    }

    /**
     * 获取属性列表
     * @param params
     * @param catelogId
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    private R attrList(@RequestParam Map<String,Object> params,@PathVariable("catelogId") Long catelogId,
      @PathVariable("attrType") String attrType){
        PageUtils page = null;
        if ("base".equalsIgnoreCase(attrType)){
            page = attrService.queryBaseAttrList(params,catelogId);
        }else {
            page = attrService.querySaleAttrList(params,catelogId);
        }


        return R.ok().put("page",page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
//    @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){

        AttrRespVo attr = attrService.getDetail(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
 //   @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){

        if (attrVo.getAttrType() == 1){
            attrService.saveBaseAttr(attrVo);
        }else if (attrVo.getAttrType() == 0){
            attrService.saveSaleAttr(attrVo);
        }


        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrvo){
		attrService.updateDetail(attrvo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
