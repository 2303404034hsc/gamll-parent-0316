package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 10:46
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    SkuInfoMapper skuInfoMapper;


    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

    }

    @Override
    public IPage<SkuInfo> getSkuInfoListByPage(Page<SkuInfo> pageParam) {
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(pageParam,null);
        return skuInfoIPage;
    }

    @Override
    public void onSale(String skuId) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id",skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(String skuId) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id",skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }
}
