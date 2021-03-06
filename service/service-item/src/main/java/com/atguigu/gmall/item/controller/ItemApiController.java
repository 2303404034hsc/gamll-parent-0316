package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ccc
 * @create 2020-08-24 14:21
 */
@RestController
public class ItemApiController {

    @Autowired
    ItemService itemService;

    @RequestMapping("api/item/{skuId}")
    Result getItem(@PathVariable("skuId") String skuId){

        Map<String,Object> map =  itemService.getItem(skuId);

        return Result.ok(map);
    }
}
