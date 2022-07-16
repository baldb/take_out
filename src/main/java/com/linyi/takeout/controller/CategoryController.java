package com.linyi.takeout.controller;

/**
 * @author linyi
 * @date 2022/7/13
 * 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }



    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete( Long id) {
        log.info("删除分类，id为：{}", id);

        //categoryService.removeById(id);
        //重点是remove方法的实现
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据ID修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("根据ID修改分类信息：{}",category.getId());
        boolean update = categoryService.updateById(category);
        if(update){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }



    /**
     * 根据条件查询菜品分类数据
     * 使用试题类型去接收参数，可用性更高，局限性小
     * type:1-->菜品 2-->套餐
     * @param category
     * @return
     * http://localhost:8080/category/list?type=1
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
    //public R<List<Category>> list(Integer type) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //queryWrapper.eq(type != null, Category::getType, type);
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }



}
