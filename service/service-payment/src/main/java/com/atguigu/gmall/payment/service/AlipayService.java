package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

/**
 * @author ccc
 * @create 2020-09-09 18:19
 */
public interface AlipayService {
    String tradePagePay(OrderInfo orderInfo);

    void save(PaymentInfo paymentInfo);

    void paySuccess(PaymentInfo paymentInfo);

    String checkPaySuccess(PaymentInfo paymentInfo);
}
