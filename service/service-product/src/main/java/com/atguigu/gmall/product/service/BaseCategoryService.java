package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-18 21:24
 */
public interface BaseCategoryService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(String category1Id);

    List<BaseCategory3> getCategory3(String category2Id);

    BaseCategoryView getCategoryView(Long category3Id);

    List<JSONObject> getBaseCategoryList();
}
