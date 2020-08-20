package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuSaleAttr;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 10:18
 */
public interface SpuSaleAttrService {
    List<SpuSaleAttr> spuSaleAttrList(String spuId);
}
