package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.DishFlavor;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.mapper.DishMapper;
import com.linyi.takeout.vo.DishDto;
import lombok.extern.slf4j.Slf4j;
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
}




