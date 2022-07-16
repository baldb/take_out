package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.DishFlavor;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.mapper.DishMapper;
import com.linyi.takeout.vo.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author linyi
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-07-14 17:51:19
*/
@SuppressWarnings({"all"})
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 添加菜品功能
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品表
        super.save(dishDto);

        //得到disId也就是口味id
        Long dishId = dishDto.getId();
        log.info("口味id：{}",dishId);

        //增加 口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //使用Stream流的方式
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //dishFlavorService.saveBatch(dishDto.getFlavors());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品ID查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        //查询到只有Dish类的数据
        DishDto dishDto = new DishDto();
        //将数据复制给dishDto
        BeanUtils.copyProperties(dish, dishDto);
        log.info("菜品分类：{}",dishDto.getCategoryName());
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //通过菜品ID去查询菜品口味的全部信息
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //最终将菜品口味信息信息全部返回
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品信息的信息及其口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
               //修改dish表
        this.updateById(dishDto);
          //修改口味表,先删除口味信息
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.eq("dish_id", dishDto.getId());
        dishFlavorService.remove(wrapper);

        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}




