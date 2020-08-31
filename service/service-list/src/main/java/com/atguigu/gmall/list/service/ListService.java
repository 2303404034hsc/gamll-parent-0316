package com.atguigu.gmall.list.service;

/**
 * @author ccc
 * @create 2020-08-31 10:01
 */
public interface ListService {
    void onSale(String skuId);

    void cancelSale(String skuId);
}
