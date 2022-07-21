package com.linyi.takeout.vo;

import com.linyi.takeout.pojo.OrderDetail;
import com.linyi.takeout.pojo.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}