package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author ccc
 * @create 2020-08-20 10:45
 */
public interface SkuInfoService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> getSkuInfoListByPage(Page<SkuInfo> pageParam);
}
