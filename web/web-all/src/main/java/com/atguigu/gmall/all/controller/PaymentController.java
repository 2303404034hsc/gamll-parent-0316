package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-09-08 8:59
 */
@Controller
public class PaymentController {

    @Autowired
    OrderFeignClient orderFeignClient;

    //跳转到支付页面
    //http://payment.gmall.com/pay.html?orderId=null
    //orderInfo?.totalAmount
    @RequestMapping("pay.html")
    public String payHtml(String orderId, Model model){
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);

        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay.html";
    }

    //回调成功页面
    //success
    @RequestMapping("success")
    public String success(String orderId, Model model){

        return "payment/success.html";
    }
}
