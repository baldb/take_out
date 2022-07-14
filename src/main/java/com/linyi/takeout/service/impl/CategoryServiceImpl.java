package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.common.CustomException;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.Setmeal;
import com.linyi.takeout.service.CategoryService;
import com.linyi.takeout.mapper.CategoryMapper;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author linyi
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-07-13 18:48:58
*/
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    //菜品
    @Autowired
    private DishService dishService;

    //套餐
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除菜品分类，删除之前考虑外键约束
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，则抛出一个异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            //已经关联，则抛出异常
            List<String> list = new ArrayList<>();
            List<Dish> list1 = dishService.list(dishLambdaQueryWrapper);
            for (Dish dish :list1) {
                list.add(dish.getName());
            }
            log.info("菜品:{}",list.toString());
            list.clear();
            throw new CustomException("当前分类下关联了菜品，不能直接删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，则抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0){
            //已经关联，则抛出异常
            List<String> setmealList = new ArrayList<>();
            List<Setmeal> list1 = setmealService.list(setmealLambdaQueryWrapper);
            for (Setmeal setmeal :list1) {
                setmealList.add(setmeal.getName());
            }
            log.info("套餐名:{}",setmealList.toString());
            setmealList.clear();
            throw new CustomException("当前分类下关联了套餐，不能直接删除");
        }
        //正常删除
        super.removeById(id);

        //删除关联该分类的一切

    }
}




