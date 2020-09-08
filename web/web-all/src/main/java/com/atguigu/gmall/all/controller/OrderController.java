package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ccc
 * @create 2020-09-07 19:33
 */
@Controller
public class OrderController {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    OrderFeignClient orderFeignClient;

    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request, Model model) {
        // 获取到通过网关验证获得的userId
        String userId = request.getHeader("userId");

        //获得购物车数据 cartInfo
        List<CartInfo> cartInfos =  cartFeignClient.getCartListCheckedByUserId(userId);

        //生成订单详情 orderDetail
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetails.add(orderDetail);
        }

        //获取地址 findUserAddressListByUserId
        List<UserAddress> userAddressList = userFeignClient.findUserAddressListByUserId(userId);



        model.addAttribute("detailArrayList",orderDetails);
        model.addAttribute("userAddressList",userAddressList);
        model.addAttribute("totalAmount",getTotalAmount(cartInfos));
        model.addAttribute("tradeNo",orderFeignClient.getTradeNo(userId));
        return "order/trade";
    }
    //获取页面总金额
    private Object getTotalAmount(List<CartInfo> cartInfos) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {
            totalAmount = totalAmount.add(cartInfo.getCartPrice());
        }
        return totalAmount;
    }
}
