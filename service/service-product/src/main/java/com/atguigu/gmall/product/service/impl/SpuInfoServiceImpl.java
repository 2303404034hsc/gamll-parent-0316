package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.mapper.BaseSaleAttrMapper;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 19:04
 */
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public IPage<SpuInfo> spuList(Page<SpuInfo> pageParam, String category3Id) {
        QueryWrapper<SpuInfo> spuInfoWrapper = new QueryWrapper<>();
        spuInfoWrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(pageParam, spuInfoWrapper);
        return spuInfoIPage;
    }


    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }
}
