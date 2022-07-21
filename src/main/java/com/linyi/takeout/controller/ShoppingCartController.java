package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linyi.takeout.common.BaseContext;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.ShoppingCart;
import com.linyi.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车数据
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("add")
    public R<ShoppingCart> addShort(@RequestBody ShoppingCart shoppingCart,
                                    HttpServletRequest request) {

//        获取用户id
//        Long userId = BaseContext.getCurrentId();
        Long userId = (Long)request.getSession().getAttribute("user");
        shoppingCart.setUserId(userId);
//        查询购物车数据是否存在
        Long dishId = shoppingCart.getDishId();

        QueryWrapper<ShoppingCart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);


        if (dishId != null) {
            wrapper.eq("dish_id", shoppingCart.getDishId());
        } else {
            wrapper.eq("setmeal_id", shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart shop = shoppingCartService.getOne(wrapper);

        if (shop != null) {
//            数量加一
            Integer number = shop.getNumber();
            shop.setNumber(number + 1);
            shoppingCartService.updateById(shop);
        } else {
//            添加数据保存到数据库
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shop = shoppingCart;
        }
        return R.success(shoppingCart);

    }


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }


    /**
     * http://localhost:8080/shoppingCart/sub
     */
    @PostMapping("/sub")
    public R<String> reduceShopCart(@RequestBody ShoppingCart shoppingCart,HttpServletRequest request){
        log.info("删除购物车中菜品的数量的菜品id为：{}",shoppingCart.getDishId());
        log.info("删除购物车中套餐的数量的菜品id为：{}",shoppingCart.getSetmealId());
        Long userId = (Long)request.getSession().getAttribute("user");
        if(shoppingCart.getDishId()!=null){
            LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId())
                    .eq(ShoppingCart::getUserId,userId);
            ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
            if(shoppingCartServiceOne.getNumber()==1){
                shoppingCartService.removeById(shoppingCartServiceOne);
            }else {
                shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()-1);
                shoppingCartService.updateById(shoppingCartServiceOne);
            }
        }else if(shoppingCart.getSetmealId()!=null){
            LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId())
                    .eq(ShoppingCart::getUserId,userId);
            ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper1);
            if(shoppingCartServiceOne.getNumber()==1){
                shoppingCartService.removeById(shoppingCartServiceOne);
            }else {
                shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()-1);
                shoppingCartService.updateById(shoppingCartServiceOne);
            }
        }
        return R.success("success~");
    }

}