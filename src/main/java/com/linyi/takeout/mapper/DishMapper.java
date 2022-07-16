package com.linyi.takeout.mapper;

import com.linyi.takeout.pojo.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author linyi
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2022-07-14 17:51:19
* @Entity com.linyi.takeout.pojo.Dish
*/
@Repository
public interface DishMapper extends BaseMapper<Dish> {

}




