package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ccc
 * @create 2020-08-20 10:45
 */
public interface SkuInfoService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> getSkuInfoListByPage(Page<SkuInfo> pageParam);

    void onSale(String skuId);

    void cancelSale(String skuId);

    SkuInfo getSkuInfo(String skuId);

    BigDecimal getSkuPrice(String skuId);

    Map<String, String> getSkuValueIdsMap(Long spuId);
}
