package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.group.AddGroup;
import com.atguigu.common.valid.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;


import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author erha
 * @email sunlightcs@gmail.com
 * @date 2021-12-28 14:03:01
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
    @NotNull(message = "修改时不能为空",groups = UpdateGroup.class)
    @Null(message = "新增时必须为空",groups = AddGroup.class)
	private Long brandId;
	/**
	 * 品牌名
	 */
    @NotBlank(message = "添加时品牌名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */

    @NotBlank(message = "url不能为空",groups = AddGroup.class)
    @URL(message = "必须是一个url",groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;

	/**
	 * 显示状态[0-不显示；1-显示]
	 */
    @ListValue(value = {0,1},message = "状态必须是0或1哦！",groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(message = "新增时状态不能为空",groups = AddGroup.class)
	private Integer showStatus;

	/**
	 * 检索首字母
	 */
    @Pattern(regexp = "^[a-zA-Z]$",message = "必须是a-z",groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(message = "新增时检索首字母不能为空",groups = AddGroup.class)
	private String firstLetter;
	/**
	 * 排序
	 */
    @Min(value = 0,message = "排序需要大于等于0",groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(message = "新增时排序不能为空",groups = AddGroup.class)
    private Integer sort;

}
