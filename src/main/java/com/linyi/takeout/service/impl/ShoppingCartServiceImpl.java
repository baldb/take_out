package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.ShoppingCart;
import com.linyi.takeout.service.ShoppingCartService;
import com.linyi.takeout.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-07-20 16:33:50
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




