package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-31 13:06
 */
public interface BaseAttrInfoService {
    List<BaseAttrInfo> getAttrList(String skuId);
}
