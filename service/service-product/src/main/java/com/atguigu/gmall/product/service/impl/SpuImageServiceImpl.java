package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 10:05
 */
@Service
public class SpuImageServiceImpl implements SpuImageService {

    @Autowired
    SpuImageMapper spuImageMapper;

    @Override
    public List<SpuImage> spuImageList(String spuId) {

        QueryWrapper<SpuImage> spuImageWrapper = new QueryWrapper<>();
        spuImageWrapper.eq("spu_id",spuId);

        return spuImageMapper.selectList(spuImageWrapper);
    }
}
