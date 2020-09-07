package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
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

    @RequestMapping("cart.html")
    public String cartHtml(HttpServletRequest request) {

        // 获取到通过网关验证获得的userId
        String userId = "";
        //首先获取临时userId,
        userId = request.getHeader("userTempId");
        //如果真正的userId有值，就覆盖
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }

        return "cart/index";
    }



    @RequestMapping("addCart.html")
    public String addCart(HttpServletRequest request,Long skuId,Integer skuNum) {
        String userId = "";
        //调用添加购物车的后台接口
        //获取网关验证获得的userId
        //首先获取临时userId,
        userId = request.getHeader("userTempId");
        //如果真正的userId有值，就覆盖
        if(StringUtils.isNotBlank(request.getHeader("userId"))){
            userId = request.getHeader("userId");
        }

        //调用后台业务接口
        if(StringUtils.isNotBlank(userId)){
            //Feign调用
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
            cartFeignClient.addCart(cartInfo);
        }

        return "redirect:http://cart.gmall.com/cart/addCart.html";
    }


}
