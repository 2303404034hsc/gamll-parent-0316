package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuImage;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-20 10:05
 */
public interface SpuImageService {
    List<SpuImage> spuImageList(String spuId);
}
