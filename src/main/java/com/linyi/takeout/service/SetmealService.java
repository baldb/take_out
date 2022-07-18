package com.linyi.takeout.service;

import com.linyi.takeout.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.takeout.vo.SetmealDto;

import java.util.List;

/**
* @author linyi
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-07-14 17:51:35
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * 添加套餐功能
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
