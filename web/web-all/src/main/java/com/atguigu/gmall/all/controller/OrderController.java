package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ccc
 * @create 2020-09-07 19:33
 */
public class OrderController {

    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request) {
        // 获取到通过网关验证获得的userId

        String userId = request.getHeader("userId");
        cartFeignClient.cartTest();
        return "order/trade";
    }
}
