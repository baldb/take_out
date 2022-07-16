package com.linyi.takeout.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * DTO：Data Transfer Object 即数据传输对象，一般用于展示层与服务层之间的数据传输
 * 用于封装除Dish类以外的其他数据
 */
@Data
public class DishDto extends Dish {

    //菜品口味集合
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品分类名称
    private String categoryName;

    private Integer copies;
}