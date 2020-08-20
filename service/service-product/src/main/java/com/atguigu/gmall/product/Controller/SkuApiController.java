package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
