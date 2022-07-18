package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.Setmeal;
import com.linyi.takeout.service.CategoryService;
import com.linyi.takeout.service.SetmealDishService;
import com.linyi.takeout.service.SetmealService;
import com.linyi.takeout.vo.SetmealDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linyi
 * @date 2022/7/17
 * 1.0
 * 套餐管理
 */

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    //套餐服务
    @Autowired
    private SetmealService setmealService;
    //套餐菜品服务
    @Autowired
    private SetmealDishService setmealDishService;

    //菜品及套餐分类服务
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto:{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 套餐信息的分页查询和带条件的分页查询
     * http://localhost:8080/setmeal/page?page=1&pageSize=10
     * http://localhost:8080/setmeal/page?page=1&pageSize=10&name=套餐名
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //@CacheEvict(value = "setmealCache", allEntries = true)
    public R<Page> querySetmealDish(int page, int pageSize, String name) {

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        Page<SetmealDto> setmealDtoPage = new Page<>();

        //根据套餐名查询分页信息
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), "name", name);
        setmealService.page(pageInfo, wrapper);

        //获取分页后的套餐信息
        List<Setmeal> records = pageInfo.getRecords();

        //将除records外的分页其他信息copy到setmealDtoPage
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        //
        List<SetmealDto> setmealDtoList = records.stream().map(item -> {

            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            //套餐分类ID
            Long categoryId = item.getCategoryId();

            //根据ID查询套餐分类的具体信息
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                //将套餐名赋值给setmealDto中的套餐名
                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;

        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }


    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    //当修改数据或添加数据时清除缓存
    @Transactional
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    @PostMapping("/status/{sta}")
    public R<String> stopshop(@PathVariable("sta") Integer sta,
                              @RequestParam("ids") List<Long> ids){
        log.info("套餐状态：{}",sta);
        log.info("套餐停售的id：{}",ids);
        /**
         * 业务逻辑：
         * 判断status进行停售和启售的动作， 输出0则关闭出售  （1-->起售）--->输出1则开启出售
         * http://localhost:8080/setmeal/status/0?ids=1415580119015145474
         */
        LambdaUpdateWrapper<Setmeal> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.in(Setmeal::getId, ids);
        dishLambdaUpdateWrapper.set(Setmeal::getStatus, sta);
        setmealService.update(dishLambdaUpdateWrapper);
        return sta==0?R.success("已将该套餐下架"):R.success("已将该套餐上架");
    }
}
