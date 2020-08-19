package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author ccc
 * @create 2020-08-19 19:03
 */
public interface SpuInfoService {
    IPage<SpuInfo> spuList(Page<SpuInfo> pageParam, String category3Id);

}
