package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    //es的查询list,包括关键字和id查询 SearchParam里面都封装了
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
            if (null != highlightFields && highlightFields.size() > 0) {
                //获取高亮文本
                HighlightField title = highlightFields.get("title");
                String highlightText = title.fragments()[0].string();
                //替换
                good.setTitle(highlightText);
            }

            goodsList.add(good);
        }


        //=======解析聚合函数================
        //Aggregation是个接口 没有get buckets的方法，需要用另外一个实现类ParsedLongTerms
//        Aggregation tmIdAgg = stringAggregationMap.get("tmIdAgg");
        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();
        //商标相关解析
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) stringAggregationMap.get("tmIdAgg");
        List<SearchResponseTmVo> trademarkList = tmIdAgg.getBuckets().stream().map(bucket -> {
            //searchResponseVo需要的是private List<SearchResponseTmVo> trademarkList;
            SearchResponseTmVo trademark = new SearchResponseTmVo();

            //获取bucket里面的商标id
//            Long tmId = Long.parseLong(bucket.getKeyAsString());
            long tmId = bucket.getKeyAsNumber().longValue();
            trademark.setTmId(tmId);

            //获取商品名称
            //这个不需要循环 ，因为每件商品都只有一个品牌 拿index(0)即可
            Map<String, Aggregation> stringAggregationMapTmIdAgg = bucket.getAggregations().asMap();
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) stringAggregationMapTmIdAgg.get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademark.setTmName(tmName);

            //获取商品logoUrl
            // 这个不需要循环 ，因为每件商品都只有一个url 拿index(0)即可
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) stringAggregationMapTmIdAgg.get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            trademark.setTmLogoUrl(tmLogoUrl);

            return trademark;
        }).collect(Collectors.toList());

        //属性相关解析
        ParsedNested attrsAgg = (ParsedNested) stringAggregationMap.get("attrsAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrsAgg.getAggregations().get("attrIdAgg");
        List<SearchResponseAttrVo> attrVoList = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
            //解析获取attrId
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //解析获取attrName
            //这个不需要循环 ，因为每件商品都只有一个attrName 拿index(0)即可
            Map<String, Aggregation> stringAggregationMapattrIdAgg = bucket.getAggregations().asMap();
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) stringAggregationMapattrIdAgg.get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //解析获取attrValue
            //这个需要循环，因为每件商品都可由有好几个属性
            //searchResponseVo-->private List<SearchResponseAttrVo> attrsList = new ArrayList<>();
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) stringAggregationMapattrIdAgg.get("attrValueAgg");
            List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket ->{
                String attrValue = attrValueBucket.getKeyAsString();
                return attrValue;
            }).collect(Collectors.toList());

            attrVo.setAttrValueList(attrValueList);

            return attrVo;
        }).collect(Collectors.toList());

        //设置值
        searchResponseVo.setGoodsList(goodsList);
        searchResponseVo.setTrademarkList(trademarkList);
        searchResponseVo.setAttrsList(attrVoList);
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
        //三级分类
        Long category3Id = searchParam.getCategory3Id();//特殊可选


        // 定义dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //must匹配 关键字
        if (StringUtils.isNotBlank(keyword)) {
            boolQueryBuilder.must(new MatchQueryBuilder("title", keyword));

            //高亮解析
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);

        }
        //三级分类 filter-term匹配
        if (null != category3Id && category3Id > 0) {
            boolQueryBuilder.filter(new TermQueryBuilder("category3Id", category3Id));
        }

        //===========属性查询===========
        if(null != props&& props.length>0){
            for (String prop : props) {
                //&props=10:奢华:风格 第一个是attrId 第二个是attrValue 第三个是attrName
                String[] split = prop.split(":");
                String attrId = split[0];
                String attrValue = split[1];
                String attrName = split[2];

                BoolQueryBuilder boolQueryBuilderNested = new BoolQueryBuilder();

                //该用must-match 还是filter
                boolQueryBuilderNested.filter(new TermQueryBuilder("attrs.attrId", attrId));
                boolQueryBuilderNested.must(new MatchQueryBuilder("attrs.attrValue", attrValue));
                boolQueryBuilderNested.must(new MatchQueryBuilder("attrs.attrName", attrName));

                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs",boolQueryBuilderNested,ScoreMode.None);

                boolQueryBuilder.filter(nestedQueryBuilder);

            }
        }

        //===========品牌查询===========
        if(null != trademark){
            //9:千奈美 第一个是tmId(long 第二个是tmName(keyword
            String[] split = trademark.split(":");
            String tmId = split[0];
//            String tmName = split[0];
            //只需要用term过滤tmId就可以了 直接使用filter过滤即可 不需要打分
            boolQueryBuilder.filter(new TermQueryBuilder("tmId", tmId));
        }

        //===========热度、价格排序===========
        //searchSourceBuilder.sort("fieldName","升序asc/降序desc");
        if(StringUtils.isNotBlank(order)){
            //1:asc
            String[] split = order.split(":");
            String type = split[0];
            String sort = split[1];
            //排序  sort(类型,升序or降序)
            if(type.equals("1")){
                type = "hotScore";
            }else{
                type = "price";
            }
            searchSourceBuilder.sort(type,sort.equals("asc")? SortOrder.ASC:SortOrder.DESC);
        }else {
            //否则采用hotScore 字段和 降序
            searchSourceBuilder.sort("hotScore",SortOrder.DESC);
        }


        //放入boolQueryBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        //==========聚合函数==============
        //聚合品牌
        TermsAggregationBuilder termsAggregationBuilderTmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));

        //聚合属性 该属性是nested的 需要另外一个aggregationBuilder (AggregationBuilders.nested)==> NestedAggregationBuilder

        NestedAggregationBuilder nestedAggregationBuilderattrsAgg = AggregationBuilders.nested("attrsAgg", "attrs")
                //有三层 第一层是没有用的 attrs
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        //第二层是attrId （只有一个值 index(0)
                        //第三层是attrName（只有一个值 index(0)）和attrValue(里面还有buckets
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                );


        searchSourceBuilder.aggregation(termsAggregationBuilderTmIdAgg);
        searchSourceBuilder.aggregation(nestedAggregationBuilderattrsAgg);

        //打印dsl语句
        System.out.println(searchSourceBuilder.toString());

        // 封装searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("goods");
        searchRequest.types("info");
        //放入searchSourceBuilder
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }
}
