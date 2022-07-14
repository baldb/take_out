package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-07-14 17:51:19
*/
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

}




