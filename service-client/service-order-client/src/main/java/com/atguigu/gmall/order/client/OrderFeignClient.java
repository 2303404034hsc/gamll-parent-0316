package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-09-08 8:45
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    @RequestMapping("api/order/inner/getTradeNo/{userId}")
    public String getTradeNo(@PathVariable("userId") String userId);

    @RequestMapping("api/order/inner/getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") String orderId);
}
