package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ccc
 * @create 2020-09-08 8:47
 */
@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    OrderService orderService;

    //获取tradeNo
    @RequestMapping("inner/getTradeNo/{userId}")
    public String getTradeNo(@PathVariable("userId") String userId) {
        String tradeNo = orderService.getTradeNo(userId);
        return tradeNo;
    }


    //订单异步提交
    //url http://api.gmall.com/api/order/auth/submitOrder?tradeNo=null
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order, String tradeNo, HttpServletRequest request) {

        // 获取到通过网关验证获得的userId
        String userId = request.getHeader("userId");

        // 校验页面交易码
        boolean b = orderService.checkTradeNo(tradeNo, userId);
        if (!b) {
            return Result.fail();
        }


        String deliveryAddress = order.getDeliveryAddress();// 地址id
        String consignee = order.getConsignee();// 收件人
        String consigneeTel = order.getConsigneeTel();//收件人电话

        //获取的是购物车中选中的cartInfos信息
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);

        if (null != cartCheckedList && cartCheckedList.size() > 0) {
            // 保存订单信息(订单表和订单详情表)
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setTotalAmount(getTotalAmount(cartCheckedList));
            orderInfo.setOrderComment("快点");
            orderInfo.setPaymentWay(PaymentWay.ONLINE.getComment());
            orderInfo.setCreateTime(new Date());
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE, 1);
            Date expireTime = instance.getTime();
            orderInfo.setExpireTime(expireTime);// 当前时间new Date()基础上+1天，过期时间-当前时间=倒计时
            orderInfo.setUserId(Long.parseLong(userId));
            orderInfo.setConsigneeTel(consigneeTel);
            orderInfo.setConsignee(consignee);
            orderInfo.setDeliveryAddress(deliveryAddress);
            orderInfo.setImgUrl(cartCheckedList.get(0).getImgUrl());
            // 生成系统外部订单号，用来和支付宝进行交易
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timeFormat = sdf.format(new Date());
            long currentTimeMillis = System.currentTimeMillis();
            String outTradeNo = "atguigu" + currentTimeMillis + timeFormat;
            orderInfo.setOutTradeNo(outTradeNo);//"atguigu"+毫秒时间戳+时间格式化字符串
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartInfo cartInfo : cartCheckedList) {
                OrderDetail orderDetail = new OrderDetail();
                BeanUtils.copyProperties(cartInfo, orderDetail);
                orderDetail.setOrderPrice(cartInfo.getCartPrice());

                // 校验此时的真实价格(调用product系统)
                BigDecimal price = productFeignClient.getSkuPrice(cartInfo.getSkuId() + "");

                // 校验此时的真实库存(调用库存系统系统),webservice

                orderDetails.add(orderDetail);
            }
            orderInfo.setOrderDetailList(orderDetails);// 封装订单详情
            OrderInfo orderInfoSave = orderService.save(orderInfo);

            // 删除购物车数据
            //获取的是购物车中选中的cartInfos信息
            //List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
            cartFeignClient.removeCartCheckedList(userId);

            return Result.ok(orderInfoSave.getId());
        }
        return Result.fail();

    }

    //根据orderId查询订单信息
    @RequestMapping("inner/getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") String orderId){

       return orderService.getOrderInfoById(orderId);
    }

    private BigDecimal getTotalAmount(List<CartInfo> cartInfos) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {
            totalAmount = totalAmount.add(cartInfo.getCartPrice());
        }

        return totalAmount;
    }

}
