package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @author ccc
 * @create 2020-09-07 13:31
 */
public interface CartInfoService {
    void addCart(CartInfo cartInfo);

    List<CartInfo> cartList(String userId);

    void checkCart(CartInfo cartInfo);

    List<CartInfo> getCartCheckedList(String userId);

    void removeCartCheckedList(String userId);
}
