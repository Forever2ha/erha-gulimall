package com.atguigu.gulimall.product.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;


/**
 * 属性分组
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 20:44:31
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @GetMapping("/{catelogId}/withattr")
    public R getCatelogAndWithItsAttrs(@PathVariable Long catelogId){

        List<AttrGroupEntity> data = attrGroupService.getCatelogAndWithItsAttrs(catelogId);

        return R.ok().put("data",data);
    }

    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities){
        attrAttrgroupRelationService.saveBatch(attrAttrgroupRelationEntities);

        return R.ok();
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@RequestParam Map<String,Object> params,@PathVariable Long attrgroupId){
        PageUtils page = attrService.getNoattrRelation(attrgroupId,params);

        if (page == null){
            return R.ok().put("page",new ArrayList<>());
        }

        return R.ok().put("page",page);
    }

    @RequestMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos){

        attrGroupService.removeRelation(attrGroupRelationVos);

        return R.ok();
    }

    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable Long attrgroupId){
        List<AttrEntity> attrEntities = attrGroupService.getRelatedAttr(attrgroupId);

        return  R.ok().put("data",attrEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
//    @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
//    @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);



        Long catelogId = attrGroup.getCatelogId();

        Long[] path = categoryService.getCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
 //   @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
