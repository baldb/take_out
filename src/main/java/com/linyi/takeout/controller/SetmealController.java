package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.BaseContext;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.pojo.Setmeal;
import com.linyi.takeout.pojo.SetmealDish;
import com.linyi.takeout.service.CategoryService;
import com.linyi.takeout.service.SetmealDishService;
import com.linyi.takeout.service.SetmealService;
import com.linyi.takeout.vo.SetmealDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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


    // http://localhost:8080/setmeal/list?categoryId=1548847120176377857&status=1
    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
//    添加套餐
//    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 回显套餐
     * @param setmealId
     * @param request
     * @return
     */
    @GetMapping("/{setmealId}")
    public R<SetmealDto> getSetmeal(@PathVariable Long setmealId,
                                HttpServletRequest request){
        Long currentId = BaseContext.getCurrentId();
        log.info("BaseContext.getCurrentId()：{}",currentId);
        log.info("session ID user：{}",request.getSession().getAttribute("user"));
        //        查询套餐表
        Setmeal setmeal = setmealService.getById(setmealId);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

//        查询套餐关联表
        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        wrapper.eq("setmeal_id", setmealId);
        List<SetmealDish> dishList = setmealDishService.list(wrapper);
        setmealDto.setSetmealDishes(dishList);
        return R.success(setmealDto);
    }



    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
//    当修改数据或添加数据时清除缓存
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        log.info("setmealDto:{}", setmealDto);
//        修改套餐表
        setmealService.updateById(setmealDto);

////        修改套餐关联表
        Long id = setmealDto.getId();
        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        wrapper.eq("setmeal_id", setmealDto.getId());
        setmealDishService.remove(wrapper);

//        保存提交过来的数据

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        List<SetmealDish> setmealDishList = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId()+"");
            return item;
        }).collect(Collectors.toList());


        setmealDishService.saveBatch(setmealDishes);

        return R.success("修改套餐成功");
    }


}
