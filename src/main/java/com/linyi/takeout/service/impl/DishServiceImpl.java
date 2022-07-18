package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.common.CustomException;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.DishFlavor;
import com.linyi.takeout.pojo.SetmealDish;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.mapper.DishMapper;
import com.linyi.takeout.service.SetmealDishService;
import com.linyi.takeout.vo.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    @Value("${reggie.path}")
    private String Path;

    /**
     * 根据id 批量删除
     * @param list
     */
    @Override
    public void delDishByIds(List<Long> list) {
        /**
         * 业务逻辑：统一使用批量删除的方法
         * 删除菜品：前提检查该菜品是否包含在某个套餐里面，没有即可删除
         * 删除：删除dish表中对应的菜品，还有DishFlavor表中对应菜品的口味，删除该菜品的图片，
         */
        //针对菜品在售状态看能不能直接删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        //状态为1，即为在售状态不可以被删除，提示先修改状态
        dishLambdaQueryWrapper1.eq(Dish::getStatus,1);
        dishLambdaQueryWrapper1.in(Dish::getId,list);
        List<Dish> list2 = dishService.list(dishLambdaQueryWrapper1);

        if(list2.isEmpty()){
            //判断该菜品是否在套餐中
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,list);
            //判断是否为空，即判断是否有在套餐中
            if(setmealDishService.list(setmealDishLambdaQueryWrapper).isEmpty()) {
                //其删除口味信息
                QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
                wrapper.in("dish_id", list);
                dishFlavorService.remove(wrapper);
                //删除本地图片信息
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.in(Dish::getId,list);
                List<Dish> list1 = super.list(dishLambdaQueryWrapper);
                for (Dish dish :list1) {
                    //根据路径创建文件对象
                    File file = new File(Path+dish.getImage());
                    //判断删除图片是否成功
                    try {
                        if(!file.delete()){
                            log.info("没有该图片:{}",dish.getImage());
                            throw new CustomException("没有该图片");
                        }
                    } catch (CustomException e) {
                        e.getMessage();
                        //e.printStackTrace();
                    }
                }
                //log.info("11111111");
                //删除菜品表的信息
                dishMapper.deleteBatchIds(list);


            }else {
                throw new CustomException("该菜品包含在套餐中,不可直接删除");
            }
        }else{
            throw new CustomException("有菜品为在售状态,不可直接删除");
        }
    }


}




