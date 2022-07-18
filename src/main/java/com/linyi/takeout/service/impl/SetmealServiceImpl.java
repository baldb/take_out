package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.common.CustomException;
import com.linyi.takeout.pojo.Setmeal;
import com.linyi.takeout.pojo.SetmealDish;
import com.linyi.takeout.service.SetmealDishService;
import com.linyi.takeout.service.SetmealService;
import com.linyi.takeout.mapper.SetmealMapper;
import com.linyi.takeout.vo.SetmealDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author linyi
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2022-07-14 17:51:35
*/
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Autowired
    private SetmealDishService setmealDishService;

    @Value("${reggie.path}")
    private String Path;

    //@Autowired
    //private SetmealService setmealService;

    /**
     * 添加套餐功能的具体业务实现
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        /**
         * 业务逻辑：接收到的数据是：套餐的基本信息+套餐菜品信息的List集合
         * 共涉及两张表，套餐表Setmea，套餐包含的菜品表：SetmealDish
         */
        //添加套餐表
        this.save(setmealDto);

        //添加套餐餐品表

        //获取套餐中包含有的菜品集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //给套餐菜品表中添加套餐id
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId()+"");
            return item;
        }).collect(Collectors.toList());

        //最后把填充完整的套餐菜品信息添加到套餐菜品表中，saveBatch：批量保存
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        /**
         * 业务逻辑：
         * 首先判断是不是为在售状态：1为在售状态不可以直接被删除
         * 删除套餐的同时删除套餐中包含的菜品，即对SetmealDish表进行删除，根据setmea_id进行删除
         * 删除套餐的本地图片
         * 最后删除Setmeal表中该套餐的信息，根据传过来的id
         */
        log.info("要删除套餐的ID集合：{}",ids.toString());
        //套餐的条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //条件是：状态为在售状态，且id的范围在ids中
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = this.list(setmealLambdaQueryWrapper);
        if(list.isEmpty()) {
            //套餐菜品关系的条件
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
            log.info("即将删除套餐中菜品");
            //删除菜品
            setmealDishService.remove(setmealDishLambdaQueryWrapper);

            log.info("即将删除套餐中的图片");
            //删除本地图片
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            setmealLambdaQueryWrapper1.in(Setmeal::getId,ids);
            List<Setmeal> list1 = this.list(setmealLambdaQueryWrapper1);
            for (Setmeal setmeal :list1) {
                //根据路径创建文件对象
                File file = new File(Path+setmeal.getImage());
                //判断删除图片是否成功
                try {
                    if(!file.delete()){
                        log.info("没有该图片:{}",setmeal.getImage());
                        throw new CustomException("没有该图片");
                    }
                } catch (CustomException e) {
                    e.getMessage();
                    //e.printStackTrace();
                }
            }
            log.info("删除套餐");
            //删除套餐
            this.removeByIds(ids);
        }else{
            throw new CustomException("套餐为在售状态，不能直接删除");
        }
    }
}




