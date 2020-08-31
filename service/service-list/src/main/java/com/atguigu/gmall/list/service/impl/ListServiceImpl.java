package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author ccc
 * @create 2020-08-31 10:01
 */
@Service
public class ListServiceImpl implements ListService {

    @Autowired
    GoodsElasticsearchRepository goodsElasticsearchRepository;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;


    @Override
    public void onSale(String skuId) {
        //优化前版本
//        onSale_bak(skuId);
        //可以使用多线程优化
        onSale_thread(skuId);

    }
    private void onSale_thread(String skuId) {
        long start = System.currentTimeMillis();
        Goods goods = new Goods();

        //skuInfo查询 需要返回值 所以用 supply
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                //查询skuInfo
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                if (null != skuInfo) {
                    goods.setId(skuInfo.getId());
                    goods.setTitle(skuInfo.getSkuName());
                    goods.setDefaultImg(skuInfo.getSkuDefaultImg());
                    goods.setPrice(skuInfo.getPrice().doubleValue());
                    goods.setCreateTime(new Date());
                }
                return skuInfo;
            }
        }, threadPoolExecutor);

        //查询goods的品牌信息 需要借助skuInfo的tmId 使用 then
        CompletableFuture<Void> completableFutureTrademark = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
                if (null != baseTrademark) {
                    goods.setTmId(baseTrademark.getId());
                    goods.setTmName(baseTrademark.getTmName());
                    goods.setTmLogoUrl(baseTrademark.getLogoUrl());
                }
            }
        }, threadPoolExecutor);

        //查询goods的分类信息 需要借助skuInfo的Category3Id 使用 then
        //查询goods的分类信息
        CompletableFuture<Void> completableFutureCategoryView = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                if (null != categoryView) {
                    goods.setCategory1Id(categoryView.getCategory1Id());
                    goods.setCategory2Id(categoryView.getCategory2Id());
                    goods.setCategory3Id(categoryView.getCategory3Id());

                    goods.setCategory1Name(categoryView.getCategory1Name());
                    goods.setCategory2Name(categoryView.getCategory2Name());
                    goods.setCategory3Name(categoryView.getCategory3Name());
                }
            }
        }, threadPoolExecutor);


        //封装平台属性的集合,不需要返回值 也不需要skuinfo的东西 异步使用
        CompletableFuture completableFutureBaseAttrInfos = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                List<BaseAttrInfo> baseAttrInfos = productFeignClient.getAttrList(skuId);
                if (null != baseAttrInfos) {
                    List<SearchAttr> searchAttrs = baseAttrInfos.stream().map(baseAttrInfo -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(baseAttrInfo.getId());
                        searchAttr.setAttrName(baseAttrInfo.getAttrName());
                        //一个sku只对应一个属性值
                        List<BaseAttrValue> baseAttrValueList = baseAttrInfo.getAttrValueList();
                        searchAttr.setAttrValue(baseAttrValueList.get(0).getValueName());
                        return searchAttr;
                    }).collect(Collectors.toList());

                    goods.setAttrs(searchAttrs);
                }
            }
        }, threadPoolExecutor);
        //allOf 所有任务完成后再插入
        CompletableFuture.allOf(completableFutureSkuInfo, completableFutureTrademark, completableFutureCategoryView, completableFutureBaseAttrInfos).join();

        //调用es的api插入数据
        goodsElasticsearchRepository.save(goods);

        long end = System.currentTimeMillis();
        System.out.println("上架消耗时间" + (end - start));
    }

    private void onSale_bak(String skuId) {
        long start = System.currentTimeMillis();
        //查询skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        //查询goods的品牌信息
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        //查询goods的分类信息
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        //封装平台属性的集合
        List<BaseAttrInfo> baseAttrInfos = productFeignClient.getAttrList(skuId);

        //使用productFeignClient从mysql中查询中es需要的数据封装给goods
        Goods goods = new Goods();

        if (null != skuInfo) {
            goods.setId(skuInfo.getId());
            goods.setTitle(skuInfo.getSkuName());
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setCreateTime(new Date());
        }

        if (null != baseTrademark) {
            goods.setTmId(baseTrademark.getId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        }

        if (null != categoryView) {
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory3Id(categoryView.getCategory3Id());

            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Name(categoryView.getCategory3Name());
        }

        if (null != baseAttrInfos) {
            List<SearchAttr> searchAttrs = baseAttrInfos.stream().map(baseAttrInfo -> {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(baseAttrInfo.getId());
                searchAttr.setAttrName(baseAttrInfo.getAttrName());
                //一个sku只对应一个属性值
                List<BaseAttrValue> baseAttrValueList = baseAttrInfo.getAttrValueList();
                searchAttr.setAttrValue(baseAttrValueList.get(0).getValueName());
                return searchAttr;
            }).collect(Collectors.toList());

            goods.setAttrs(searchAttrs);
        }

        //调用es的api插入数据
        goodsElasticsearchRepository.save(goods);
        long end = System.currentTimeMillis();
        System.out.println("上架消耗时间" + (end - start));
    }

    @Override
    public void cancelSale(String skuId) {
        //调用es的api删除数据,根据skuId删除
        goodsElasticsearchRepository.deleteById(Long.parseLong(skuId));
    }

    @Override
    public void hotScore(String skuId) {

        Long hotScore = 0L;

        //查询原来的热度值
        //调用es的api进行自增 zset有专门的
        hotScore = redisTemplate.opsForZSet().incrementScore("hotScore", "sku:" + skuId, 1).longValue();

        //判断是否达到阈值，每10次更新一次es
        if(hotScore % 10 == 0){
            //先使用es API查询出来
            Optional<Goods> goodsOptional = goodsElasticsearchRepository.findById(Long.parseLong(skuId));

            Goods goods = goodsOptional.get();

            //将热度重新设置值，覆盖掉原来的值
            goods.setHotScore(hotScore);
            goodsElasticsearchRepository.save(goods);
        }
    }
}
