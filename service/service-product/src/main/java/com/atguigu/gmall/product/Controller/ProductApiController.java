package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-18 20:30
 */
@RestController
@RequestMapping("api/product")
@CrossOrigin
public class ProductApiController {

    @Autowired
    SkuInfoService skuInfoService;

    @RequestMapping("testApiController")
    public String testApiController() {
        return "testApiController";
    }

    @RequestMapping("inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") String skuId){

        SkuInfo skuInfo = skuInfoService.getSkuInfo(skuId);
        return skuInfo;
    }
}
