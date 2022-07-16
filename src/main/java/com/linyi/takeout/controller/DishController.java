package com.linyi.takeout.controller;

/**
 * @author linyi
 * @date 2022/7/16
 * 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.R;
import com.linyi.takeout.mapper.DishMapper;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.pojo.Dish;
import com.linyi.takeout.service.CategoryService;
import com.linyi.takeout.service.DishFlavorService;
import com.linyi.takeout.service.DishService;
import com.linyi.takeout.vo.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    //菜品
    @Autowired
    private DishService dishService;

    //菜品口味
    @Autowired
    private DishFlavorService dishFlavorService;

    //菜品分类
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品流程：
     * 页面发送一个请求（http://localhost:8080/category/list?type=1）：获取菜品的所有类别，回显给页面，供管理者选择
     * 页面发送图片上传，把图片上传给服务器
     * 随后马上进行图片下载，从服务器下载到页面上，即回显
     * 点击保存按钮，发送JSON格式，后端接收数据并存储到数据库
     * 这边上传图片要返回图片名，保存按钮中返回的是图片名称
     *
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }



    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        log.info("添加条件前的：{}",pageInfo);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);
        log.info("添加条件后的：{}",pageInfo);

        //对象拷贝，只拷贝自己需要的数据，这边定义除records属性外，其他都需要拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

//        得到总的记录数
        List<Dish> records = pageInfo.getRecords();


        // 个人理解：定义一个List集合用来存放DishDto类型的数据
        List<DishDto> dtoList = records.stream().map(item -> {
            //创建 一个这个类型的对象，用来存储数据
            DishDto dishDto = new DishDto();

            //将不完整的数据先复制/赋值到该对象中
            BeanUtils.copyProperties(item, dishDto);

//            查询数据库中的CategoryName
            Long id = item.getCategoryId();
            if (id != null) {
                Category category = categoryService.getById(id);
                //将对象中不完整的数据补全
                dishDto.setCategoryName(category.getName());
            }
            //返回一个完整的对象
            return dishDto;
            //最后将对象一个一个的添加到新的List集合中
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dtoList);

        return R.success(dishDtoPage);

    }

    /**
     * 法二：一般不用这种在原有类的基础上去增加一个属性标注为非数据库字段。
     * @TableField(exist = false)
     * private String categoryName;
     *
     * 而是使用上方这种继承的方式去重新生成一个类去做相关的操作
     *
     * @return
     */
    //@GetMapping("/page")
    //public R<Page> page(int page, int pageSize, String name) {
    //
    //    Page<Dish> dishPage = new Page<>(page,pageSize);
    //    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //    dishLambdaQueryWrapper.eq(name!=null,Dish::getName,name);
    //    dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
    //    Page<Dish> page1 = dishService.page(dishPage,dishLambdaQueryWrapper);
    //    log.info("查询出来菜品信息：{}",page1.getRecords());
    //    List<Dish> records = page1.getRecords();
    //    for (Dish dish :records) {
    //        //获取每个菜品的分类类别的id
    //        //dish.getCategoryId()
    //        Category category = categoryService.getById(dish.getCategoryId());
    //        dish.setCategoryName(category.getName());
    //    }
    //    dishPage.setRecords(records);
    //    log.info("添加分类后菜品信息：{}",dishPage.getRecords());
    //    return R.success(dishPage);
    //}

    /**
     * 根据id查询菜品具体信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
/*
       删除所有菜品缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
*/

//        删除单个菜品缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        //redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }



    /**
     * 单个删除，批量删除菜品
     */
    @DeleteMapping
    public R<String> delDish(@RequestParam("ids") List<Long> ids){
        log.info("删除的id：{}",ids);
        /**
         * 业务逻辑：统一使用批量删除的方法
         * 删除菜品：前提检查该菜品是否包含在某个套餐里面，没有即可删除
         * 删除：删除dish表中对应的菜品，还有DishFlavor表中对应菜品的口味，删除该菜品的图片，
         */

        return null;
    }

    /**
     * 单个停售,批量停售。和启售
     * http://localhost:8080/dish/status/0?ids=1397862198033297410
     */
    @PostMapping("/status/{sta}")
    public R<String> stopshop(@PathVariable("sta") Integer sta,
                              @RequestParam("ids") List<Long> ids){
        log.info("状态：{}",sta);
        log.info("停售的id：{}",ids);
        /**
         * 业务逻辑：
         * 判断status进行停售和启售的动作， 输出0则关闭出售  （1-->起售）--->输出1则开启出售
         */
            LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            dishLambdaUpdateWrapper.in(Dish::getId, ids);
            dishLambdaUpdateWrapper.set(Dish::getStatus, sta);
            dishService.update(dishLambdaUpdateWrapper);
        return sta==0?R.success("已将该商品下架"):R.success("已将该商品上架");
    }
}
