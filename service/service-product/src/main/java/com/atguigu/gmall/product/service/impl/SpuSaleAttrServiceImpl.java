package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 10:18
 */
@Service
public class SpuSaleAttrServiceImpl implements SpuSaleAttrService {

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(String spuId) {
        QueryWrapper<SpuSaleAttr> spuSaleAttrWrapper = new QueryWrapper<>();
        spuSaleAttrWrapper.eq("spu_id",spuId);
        //查询spuSaleAttrs集合
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(spuSaleAttrWrapper);
        //需要把spuSaleAttrValue封装进去
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            Long attrId= spuSaleAttr.getBaseSaleAttrId();

            QueryWrapper<SpuSaleAttrValue> spuSaleAttrValueWrapper = new QueryWrapper<>();
            spuSaleAttrValueWrapper.eq("spu_id",spuId);
            spuSaleAttrValueWrapper.eq("base_sale_attr_id",attrId);
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(spuSaleAttrValueWrapper);

            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }

        return spuSaleAttrs;
    }
}
