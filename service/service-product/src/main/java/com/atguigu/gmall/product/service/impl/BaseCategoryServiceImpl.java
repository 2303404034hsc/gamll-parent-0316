package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.CategoryServiceMapper;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 9:55
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {

    @Autowired
    CategoryServiceMapper categoryServiceMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return categoryServiceMapper.selectList(null);
    }
}
