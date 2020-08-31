package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.stereotype.Service;

/**
 * @author ccc
 * @create 2020-08-31 10:01
 */
@Service
public class ListServiceImpl implements ListService {


    @Override
    public void onSale(String skuId) {

        //从mysql中查询中es需要的数据封装给goods

        Goods goods = new Goods();

        //调用es的api插入数据
    }

    @Override
    public void cancelSale(String skuId) {
        //调用es的api删除数据,根据skuId删除
    }
}
