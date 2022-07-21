package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.OrderDetail;
import com.linyi.takeout.service.OrderDetailService;
import com.linyi.takeout.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-07-21 09:26:27
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




