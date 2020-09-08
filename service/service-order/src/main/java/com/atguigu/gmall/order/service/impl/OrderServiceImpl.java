package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author ccc
 * @create 2020-09-08 14:19
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public OrderInfo save(OrderInfo orderInfo) {

        //插入orderInfo表
        orderInfoMapper.insert(orderInfo);
        //插入orderDetails表
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }

        return orderInfo;
    }

    @Override
    public boolean checkTradeNo(String tradeNo, String userId) {
        boolean b = false;
        String tradeNoFromCache = (String) redisTemplate.opsForValue().get("user:" + userId + ":tradeNo");
        if (tradeNo.equals(tradeNoFromCache)) {
            b = true;
            redisTemplate.delete("user:" + userId + ":tradeNo");
        }
        return b;
    }

    @Override
    public String getTradeNo(String userId) {
        //生成TradeNo key: user:userId:tradeNo   value:tradeNo(UUID
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("user:" + userId + ":tradeNo", tradeNo);
        return tradeNo;
    }

    @Override
    public OrderInfo getOrderInfoById(String orderId) {
        return orderInfoMapper.selectById(orderId);
    }
}
