package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.AttrInfoMapper;
import com.atguigu.gmall.product.mapper.AttrValueMapper;
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

    @Autowired
    AttrValueMapper attrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id) {
        QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_level",3);
        queryWrapper.eq("category_id",category3Id);
        List<BaseAttrInfo> baseAttrInfos = attrInfoMapper.selectList(queryWrapper);
        //属性值集合
        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
            Long attrId = baseAttrInfo.getId();

            QueryWrapper<BaseAttrValue> queryWrapperValue = new QueryWrapper<>();
            queryWrapperValue.eq("attr_id",attrId);
            List<BaseAttrValue> baseAttrInfoValues = attrValueMapper.selectList(queryWrapperValue);
            baseAttrInfo.setAttrValueList(baseAttrInfoValues);
        }
        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        Long attrId = baseAttrInfo.getId();
        if(null != attrId){
            //修改属性表
            attrInfoMapper.updateById(baseAttrInfo);
            //删除属性值
            QueryWrapper<BaseAttrValue> attrValueWrapper = new QueryWrapper<>();
            attrValueWrapper.eq("attr_id",attrId);
            attrValueMapper.delete(attrValueWrapper);
        }else{
            //添加属性值

            attrInfoMapper.insert(baseAttrInfo);
            attrId = baseAttrInfo.getId();
        }
        //保存属性值表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(attrId);
            attrValueMapper.insert(baseAttrValue);
        }


    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        QueryWrapper<BaseAttrValue> baseAttrValueWrapper = new QueryWrapper<>();
        baseAttrValueWrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValues = attrValueMapper.selectList(baseAttrValueWrapper);
        return baseAttrValues;
    }

    @Override
    public void deleteAttrInfo(Long attrValueId) {
        attrValueMapper.deleteById(attrValueId);
    }
}
