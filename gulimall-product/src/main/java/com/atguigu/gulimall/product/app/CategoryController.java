package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品三级分类
 *
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 20:44:29
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    //以树形结构返回商品分类
    @RequestMapping("/list/tree")
    public R listTree(){
        List<CategoryEntity> entities = categoryService.listWithTree();
        return R.ok().put("data",entities);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
//    @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
 //   @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update/sort")
    //  @RequiresPermissions("product:category:update")
    public R updateBatch(@RequestBody List<CategoryEntity> categoryEntityList){
        categoryService.updateCascadeSort(categoryEntityList);

        return R.ok();
    }


    @RequestMapping("/updateMenus")
    public R updateMenus(@RequestBody CategoryEntity[] categoryEntities){
        categoryService.updateBatchById(Arrays.asList(categoryEntities));
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        // TODO: 2023/1/23 检查引用再删除
		categoryService.removeByIds(Arrays.asList(catIds));
        return R.ok();
    }

    @RequestMapping("/deleteMenus")
    public R deleteMenus(@RequestBody Long[] catIds){
        categoryService.removeMenus(Arrays.asList(catIds));
        return R.ok();
    }

}
