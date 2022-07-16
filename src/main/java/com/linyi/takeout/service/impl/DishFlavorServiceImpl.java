package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.DishFlavor;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-07-15 19:59:58
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




