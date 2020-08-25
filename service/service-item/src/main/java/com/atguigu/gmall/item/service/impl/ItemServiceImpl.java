package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
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

/**
 * @author ccc
 * @create 2020-08-24 18:23
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getItem(String skuId) {

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

        return map;
    }
}
