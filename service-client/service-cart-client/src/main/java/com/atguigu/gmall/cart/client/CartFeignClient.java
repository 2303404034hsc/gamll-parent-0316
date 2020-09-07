package com.atguigu.gmall.cart.client;

import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-09-04 23:49
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {

    @RequestMapping("api/cart/cartTest")
    void cartTest();

    @RequestMapping("api/cart/addCart")
    void addCart(@RequestBody CartInfo cartInfo);
}
