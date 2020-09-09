package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.AlipayService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ccc
 * @create 2020-09-09 18:21
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public String tradePagePay(OrderInfo orderInfo) {

        String form = "";

        // 调用支付宝接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置异步通知地址
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        //设置同步回调地址
        request.setReturnUrl(AlipayConfig.return_payment_url);
        //使用Map代替拼接
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
        paramMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        paramMap.put("total_amount",0.01);
        StringBuffer productName = new StringBuffer();
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            productName.append(orderDetail.getSkuName() + ",");
        }
        paramMap.put("subject",productName.toString());
        request.setBizContent(JSON.toJSONString(paramMap));
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
            form = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }

    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insert(paymentInfo);

    }

    @Override
    public void paySuccess(PaymentInfo paymentInfo) {
        //更新payment_info表
        QueryWrapper<PaymentInfo> paymentInfoWrapper = new QueryWrapper<>();
        paymentInfoWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());
        paymentInfoMapper.update(paymentInfo,paymentInfoWrapper);
    }

    @Override
    public String checkPaySuccess(PaymentInfo paymentInfo) {

        QueryWrapper<PaymentInfo> paymentInfoWrapper = new QueryWrapper<>();
        paymentInfoWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());
        PaymentInfo paymentInfoFromDb = paymentInfoMapper.selectOne(paymentInfoWrapper);

        return paymentInfoFromDb.getPaymentStatus();
    }
}
