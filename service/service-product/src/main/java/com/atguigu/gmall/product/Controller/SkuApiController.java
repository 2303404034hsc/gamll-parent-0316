package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    //4.获取sku分页列表
    // http://api.gmall.com/admin/product/list/{page}/{limit}
    @RequestMapping("list/{page}/{limit}")
    public Result getSkuInfoListByPage(@PathVariable("page") Long page,@PathVariable("limit") Long limit) {
        Page<SkuInfo> pageParam = new Page<>(page,limit);
        IPage<SkuInfo> skuInfoListByPage = skuInfoService.getSkuInfoListByPage(pageParam);
        return Result.ok(skuInfoListByPage);
    }

    //5.上架
    // http://api.gmall.com/admin/product/onSale/{skuId}
    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") String skuId) {
        skuInfoService.onSale(skuId);
        return Result.ok().message("上架成功");
    }

    //6.下架
    // http://api.gmall.com/admin/product/cancelSale/{skuId}
    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") String skuId) {
        skuInfoService.cancelSale(skuId);
        return Result.ok().message("下架成功");
    }



}
