package com.atguigu.gmall.product.client;

import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-08-24 14:47
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @RequestMapping("api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") String skuId);

}
