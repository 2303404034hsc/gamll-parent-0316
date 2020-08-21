package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 11:22
 */
public interface AttrInfoService {
    List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void deleteAttrInfo(Long attrValueId);
}
