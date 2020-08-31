package com.atguigu.gmall.list.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-08-31 8:43
 */
@FeignClient(value = "service-list")
public interface ListFeignClient {

    @RequestMapping("api/list/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") String skuId);

    @RequestMapping("api/list/onSale/{skuId}")
    void onSale(@PathVariable("skuId") String skuId);

    @RequestMapping("api/list/hotScore/{skuId}")
    void hotScore(@PathVariable("skuId") String skuId);
}
