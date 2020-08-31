package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author ccc
 * @create 2020-08-24 18:23
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    ListFeignClient listFeignClient;

    /**
     * 使用多线程优化
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItem(String skuId) {
        //普通的缓存方法
//        Map<String,Object> map = getItemBak(skuId);
        //多线程优化后的方法 组合式异步编程
        Map<String, Object> map = getItemMultiThread(skuId);

        //通过listFeign 调用热度值接口
        listFeignClient.hotScore(skuId);

        return map;
    }

    private Map<String, Object> getItemMultiThread(String skuId) {
        long start = System.currentTimeMillis();
        Map<String,Object> map = new HashMap<>();

        //商品价格查询 不需要返回值
        CompletableFuture completableFutureSkuPrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = new BigDecimal("0");
                price = productFeignClient.getSkuPrice(skuId);
                map.put("price",price);
            }
        },threadPoolExecutor);

        //skuInfo查询 需要返回值 所以用 supply
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo =  productFeignClient.getSkuInfo(skuId);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);

        //查询分类列表 不需要返回值，需要借助skuInfo里面的category3Id
        CompletableFuture<Void> categoryView = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", baseCategoryView);
            }
        },threadPoolExecutor);
        //查询销售属性 不需要，需要借助skuInfo里面的spuId
        CompletableFuture<Void> spuSaleAttrList = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getMySpuSaleAttrs(skuInfo.getSpuId(), skuId);
                map.put("spuSaleAttrList", spuSaleAttrs);
            }
        },threadPoolExecutor);

        //销售属性对应sku的hash表
        CompletableFuture<Void> valuesSkuJson = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {

                Map<String, String> valueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
                map.put("valuesSkuJson", JSON.toJSONString(valueIdsMap));
            }
        },threadPoolExecutor);

        CompletableFuture.allOf(completableFutureSkuPrice,completableFutureSkuInfo,categoryView,spuSaleAttrList,valuesSkuJson).join();
        long end = System.currentTimeMillis();
        System.out.println("消耗时间" +(end - start));
        return map;
    }

    private Map<String, Object> getItemBak(String skuId) {

        long start = System.currentTimeMillis();
        //商品详情汇总封装基础数据
        Map<String,Object> map = new HashMap<>();

        //商品价格查询
        BigDecimal price = new BigDecimal("0");
        price = productFeignClient.getSkuPrice(skuId);
        map.put("price",price);

        //查看sku详细信息sku_info
        SkuInfo skuInfo =  productFeignClient.getSkuInfo(skuId);
        map.put("skuInfo",skuInfo);

        //查询分类列表
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        map.put("categoryView",baseCategoryView);

        //查询销售属性
        List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getMySpuSaleAttrs(skuInfo.getSpuId(),skuId);
        map.put("spuSaleAttrList",spuSaleAttrs);

        //销售属性对应sku的hash表
        Map<String,String> valueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        map.put("valuesSkuJson", JSON.toJSONString(valueIdsMap));

        long end = System.currentTimeMillis();
        System.out.println("消耗时间" +(end - start));
        return map;
    }
}
