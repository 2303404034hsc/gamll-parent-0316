package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.AttrInfoMapper;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 11:22
 */
@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    AttrInfoMapper attrInfoMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id) {
        QueryWrapper<BaseAttrInfo> baseAttrInfoWrapper = new QueryWrapper<>();
        baseAttrInfoWrapper.eq("category_id",category3Id);
        baseAttrInfoWrapper.eq("category_level",3);
        List<BaseAttrInfo> baseAttrInfos = attrInfoMapper.selectList(baseAttrInfoWrapper);
        return baseAttrInfos;
    }
}
