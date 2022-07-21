package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.BaseContext;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.OrderDetail;
import com.linyi.takeout.pojo.Orders;
import com.linyi.takeout.pojo.User;
import com.linyi.takeout.service.OrderDetailService;
import com.linyi.takeout.service.OrderService;
import com.linyi.takeout.service.UserService;
import com.linyi.takeout.vo.OrdersDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;


    /**
     * 后台员工分页查看订单信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        log.info("page = {},pageSize = {}, number = {}", page, pageSize, number);

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();

        lqw.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        lqw.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        //查询订单基本信息
        Page<Orders> ordersPage = orderService.page(pageInfo, lqw);

        //把基本信息拷贝到OrdersDto对象中
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> ordersRecords = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersRecords.stream().map((orderRecord) -> {
            OrdersDto ordersDto = new OrdersDto();
            //拷贝对象
            BeanUtils.copyProperties(orderRecord, ordersDto);
            //获取订单id
            Long orderId = orderRecord.getId();
            //通过订单id查询该订单下对应的菜品/套餐
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(orderDetails);

            //根据用户id获取用户名
            Long userId = orderRecord.getUserId();
            User user = userService.getById(userId);
            if (StringUtils.isNotEmpty(user.getName())) {
                ordersDto.setUserName(user.getName());
            }
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersPage);
    }


    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {

        orderService.saveWithOrder(orders);
        return R.success("用户下单成功");
    }


    /**
     * 查询订单明细表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("userPage")
    public R<Page> orderDetail(Integer page, Integer pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<OrdersDto>();

        Long currentId = BaseContext.getCurrentId();
//        查询订单数据
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentId);
        wrapper.orderByDesc("checkout_time");
        Page<Orders> ordersPage = orderService.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> ordersList = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = ordersList.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            Long orderId = item.getId();

            if (orderId != null) {
//                        查询订单明细表
                QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id", orderId);
                List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
                ordersDto.setOrderDetails(orderDetailList);
            }

            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);


    }

    /**
     * http://localhost:8080/order
     * 修改订单状态
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     */
    @PutMapping
    public R<String> orderStatus(@RequestBody Orders orders){
        log.info("订单ID：{}",orders.getId());
        log.info("要把订单状态改成：{}",orders.getStatus());
        switch (orders.getStatus()){
            case 3 :
                LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                ordersLambdaUpdateWrapper.set(Orders::getStatus,orders.getStatus());
                ordersLambdaUpdateWrapper.eq(Orders::getId,orders.getId());
                orderService.update(ordersLambdaUpdateWrapper);
                return R.success("已派送～");
            case 4 :
                LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                ordersLambdaUpdateWrapper1.set(Orders::getStatus,orders.getStatus());
                ordersLambdaUpdateWrapper1.eq(Orders::getId,orders.getId());
                orderService.update(ordersLambdaUpdateWrapper1);
                return R.success("已完成~");
        }
        return R.error("没有此状态～");
    }

}