package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ccc
 * @create 2020-09-04 20:26
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request) {
        // 获取到通过网关验证获得的userId

        String userId = request.getHeader("userId");
        cartFeignClient.cartTest();
        return "order/trade";
    }

    @RequestMapping("addCart.html")
    public String addCart(HttpServletRequest request) {
        String userId = "";
        //调用添加购物车的后台接口
        //获取网关验证获得的userId
        userId = request.getHeader("userTempId");
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }

        if(StringUtils.isNotBlank(userId)){
            //调用后台业务接口
        }

        return "redirect:http://cart.gmall.com/cart/addCart.html";
    }
}
