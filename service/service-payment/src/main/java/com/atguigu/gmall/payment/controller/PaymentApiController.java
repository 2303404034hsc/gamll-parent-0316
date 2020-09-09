package com.atguigu.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author ccc
 * @create 2020-09-08 20:56
 */
@RestController
@RequestMapping("api/payment")
public class PaymentApiController {


    @Autowired
    AlipayService alipayService;

    @Autowired
    OrderFeignClient orderFeignClient;

    @RequestMapping("alipay/callback/return")
    public void  alipayCallback(HttpServletRequest request, HttpServletResponse response){

        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String callback_content = request.getQueryString();

        //封装支付结果
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setCallbackContent(callback_content);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setPaymentType(PaymentStatus.PAID.toString());

        //进行幂等性检查
        String success = alipayService.checkPaySuccess(paymentInfo);
        if(!success.equals("PAID")){
            //修改支付状态
            alipayService.paySuccess(paymentInfo);
        }


        //TODO 修改订单状态（Mq

        //TODO 锁定库存（MQ

        // 返回成功页面
//        String form = "<form name='punchout_form' method='get' action='http://payment.gmall.com/success'>\n" +
//                "</form>\n" +
//                "<script>document.forms[0].submit();</script>";
//        return form;
        try {
            response.sendRedirect("http://payment.gmall.com/success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @RequestMapping("alipay/submit/{orderId}")
    public String aliPay(@PathVariable("orderId") String orderId, HttpServletRequest request) {

        String userId = request.getHeader("userId");

        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);

        // 返回给页面的是一个支付宝的表单
        String form = alipayService.tradePagePay(orderInfo);

        // 保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        //=== 支付宝 ===
        // paymentInfo.setCallbackTime();
        // paymentInfo.setTradeNo();
        // paymentInfo.setCallbackContent();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(orderInfo.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setOrderId(Long.parseLong(orderId));
        paymentInfo.setPaymentType(PaymentType.ALIPAY.getComment());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        alipayService.save(paymentInfo);

        return form;

    }

}
