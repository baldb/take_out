package com.linyi.takeout.controller;

/**
 * @author linyi
 * @date 2022/7/16
 * 1.0
 */

import com.linyi.takeout.common.R;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.vo.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜品管理
 */

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    //菜品
    @Autowired
    private DishService dishService;

    //菜品口味
    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品流程：
     * 页面发送一个请求（http://localhost:8080/category/list?type=1）：获取菜品的所有类别，回显给页面，供管理者选择
     * 页面发送图片上传，把图片上传给服务器
     * 随后马上进行图片下载，从服务器下载到页面上，即回显
     * 点击保存按钮，发送JSON格式，后端接收数据并存储到数据库
     * 这边上传图片要返回图片名，保存按钮中返回的是图片名称
     *
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

}
