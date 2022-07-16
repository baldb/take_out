package com.linyi.takeout.service;

import com.linyi.takeout.pojo.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.takeout.vo.DishDto;

import java.util.List;

/**
* @author linyi
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-07-14 17:51:19
*/
public interface DishService extends IService<Dish> {

    /**
     * 添加菜品功能
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     *根据菜品id返回具体菜品信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);


    /**
     * 修改商品功能
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);


    /**
     * 根据id删除
     */
    void  delDishByIds(List<Long> list);
}
