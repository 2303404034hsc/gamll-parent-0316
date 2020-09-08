package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @author ccc
 * @create 2020-09-08 14:19
 */
public interface OrderService {
    OrderInfo save(OrderInfo orderInfo);

    boolean checkTradeNo(String tradeNo, String userId);

    String getTradeNo(String userId);

    OrderInfo getOrderInfoById(String orderId);
}
