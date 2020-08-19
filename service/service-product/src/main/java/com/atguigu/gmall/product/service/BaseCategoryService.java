package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-18 21:24
 */
public interface BaseCategoryService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2();
}
