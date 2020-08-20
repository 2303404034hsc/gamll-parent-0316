package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 9:56
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuApiController {

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuInfoService skuInfoService;

    //根据spuId获取图片列表
    //http://api.gmall.com/admin/product/spuImageList/{spuId}
    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") String spuId) {

        List<SpuImage> spuImages =  spuImageService.spuImageList(spuId);
        return Result.ok(spuImages);
    }

    //根据spuId获取销售属性
    //http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") String spuId) {

        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrService.spuSaleAttrList(spuId);

        return Result.ok(spuSaleAttrs);
    }

    //添加sku
    //http://api.gmall.com/admin/product/saveSkuInfo
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {

        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }
}
