package com.atguigu.gmall.item.client;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author ccc
 * @create 2020-08-22 16:36
 */
@FeignClient(value = "service-item")
public interface ItemFeignClient {

    @RequestMapping("api/item/{skuId}")
    Result getItem(@PathVariable("skuId") String skuId);


}
