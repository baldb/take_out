package com.linyi.takeout.service;

import com.linyi.takeout.pojo.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author linyi
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-07-21 09:26:41
*/
public interface OrderService extends IService<Orders> {


    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    void saveWithOrder(Orders orders);
}
