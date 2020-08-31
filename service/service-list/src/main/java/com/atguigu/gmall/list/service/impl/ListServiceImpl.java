package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    RestHighLevelClient restHighLevelClient;


    //es上架
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

    //es下架
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
        if (hotScore % 10 == 0) {
            //先使用es API查询出来
            Optional<Goods> goodsOptional = goodsElasticsearchRepository.findById(Long.parseLong(skuId));

            Goods goods = goodsOptional.get();

            //将热度重新设置值，覆盖掉原来的值
            goods.setHotScore(hotScore);
            goodsElasticsearchRepository.save(goods);
        }
    }

    //es的查询list
    @Override
    public SearchResponseVo list(SearchParam searchParam) {

        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //生成dsl语句
        SearchRequest searchRequest = buildQueryDsl(searchParam);

        // 执行查询操作，调用search方法
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析返回结果
        searchResponseVo = parseSearchResult(search);

        return searchResponseVo;
    }

    /**
     * 解析返回结果
     *
     * @param search
     * @return
     */
    private SearchResponseVo parseSearchResult(SearchResponse search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = search.getHits();
        List<Goods> goodsList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Goods good = JSON.parseObject(sourceAsString, Goods.class);

            //解析高亮 将高亮的title替代good的title
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //有些商品没有高亮字段 需要先进行判断
            if(null !=highlightFields){
                //获取高亮文本
                HighlightField title = highlightFields.get("title");
                String highlightText = title.fragments()[0].string();
                //替换
                good.setTitle(highlightText);
            }

            goodsList.add(good);
        }
        searchResponseVo.setGoodsList(goodsList);

        return searchResponseVo;
    }

    /**
     * 封装dsl搜索请求语句
     *
     * @param searchParam
     * @return
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        //搜索参数
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();
        String order = searchParam.getOrder();
        //三级分类和关键字有且仅有一个
        String keyword = searchParam.getKeyword();//特殊可选
        Long category3Id = searchParam.getCategory3Id();//特殊可选


        // 定义dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (StringUtils.isNotBlank(keyword)) {
            boolQueryBuilder.must(new MatchQueryBuilder("title", keyword));

            //高亮解析
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);


        }
        if (null != category3Id && category3Id > 0) {
            boolQueryBuilder.filter(new TermQueryBuilder("category3Id", category3Id));
        }




        searchSourceBuilder.query(boolQueryBuilder);
        System.out.println(searchSourceBuilder.toString());

        // 封装searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("goods");
        searchRequest.types("info");
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }
}
