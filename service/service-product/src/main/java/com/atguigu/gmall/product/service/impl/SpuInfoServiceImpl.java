package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
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

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public IPage<SpuInfo> spuList(Page<SpuInfo> pageParam, String category3Id) {
        QueryWrapper<SpuInfo> spuInfoWrapper = new QueryWrapper<>();
        spuInfoWrapper.eq("category3_id", category3Id);
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(pageParam, spuInfoWrapper);
        return spuInfoIPage;
    }


    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        //1、保存spuInfo表
        spuInfoMapper.insert(spuInfo);
        //返回主键id
        Long spuId = spuInfo.getId();

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (null != spuImageList) {
            for (SpuImage spuImage : spuImageList) {
                //2、保存spuImage表(没有fdfs暂时为空
                spuImage.setSpuId(spuId);
                spuImageMapper.insert(spuImage);
            }
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (null != spuSaleAttrList) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                //3、保存spu_sale_attr表
                spuSaleAttr.setSpuId(spuId);
                spuSaleAttrMapper.insert(spuSaleAttr);

                //4、保存spu_sale_value表
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (null != spuSaleAttrValueList) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                        spuSaleAttrValue.setSpuId(spuId);
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }

    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, String skuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrs;
    }
}
