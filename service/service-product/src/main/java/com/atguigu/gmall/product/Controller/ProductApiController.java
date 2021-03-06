package com.atguigu.gmall.product.Controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Autowired
    BaseCategoryService baseCategoryService;

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseTrademarkService trademarkService;

    @RequestMapping("testApiController")
    public String testApiController() {
        return "testApiController";
    }

    @RequestMapping("inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") String skuId) {

        SkuInfo skuInfo = skuInfoService.getSkuInfo(skuId);
        return skuInfo;
    }

    @RequestMapping("inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") String skuId) {
        BigDecimal price = skuInfoService.getSkuPrice(skuId);
        return price;
    }

    @RequestMapping("inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id) {

        BaseCategoryView getCategoryView = baseCategoryService.getCategoryView(category3Id);
        return getCategoryView;
    }

    @RequestMapping("inner/getMySpuSaleAttrs/{spuId}/{skuId}")
    List<SpuSaleAttr> getMySpuSaleAttrs(@PathVariable("spuId") Long spuId, @PathVariable("skuId") String skuId) {

        List<SpuSaleAttr> spuSaleAttrs = spuInfoService.getSpuSaleAttrListCheckBySku(spuId, skuId);
        return spuSaleAttrs;
    }

    @RequestMapping("inner/getSkuValueIdsMap/{spuId}")
    Map<String, String> getSkuValueIdsMap(@PathVariable("spuId") Long spuId) {
        Map<String, String> valueIdsMap = skuInfoService.getSkuValueIdsMap(spuId);
        return valueIdsMap;
    }

    @GetMapping("getBaseCategoryList")
    Result getBaseCategoryList() {

        List<JSONObject> jsonObjects = baseCategoryService.getBaseCategoryList();

        return Result.ok(jsonObjects);
    }

    @GetMapping("getAttrList/{skuId}")
    List<BaseAttrInfo> getAttrList(@PathVariable("skuId") String skuId) {
        return baseAttrInfoService.getAttrList(skuId);
    }


    //根据品牌id获取品牌信息
    @GetMapping("/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId") Long tmId){
        BaseTrademark trademark=trademarkService.getTrademark(tmId);
        return trademark;
    }

}
