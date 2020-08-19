package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
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
    BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {
        QueryWrapper<BaseCategory2> baseCategory2Wrapper = new QueryWrapper<>();
        baseCategory2Wrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(baseCategory2Wrapper);
        return baseCategory2s;
    }

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {

        QueryWrapper<BaseCategory3> baseCategory3Wrapper = new QueryWrapper<>();
        baseCategory3Wrapper.eq("category2_id",category2Id);
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(baseCategory3Wrapper);
        return baseCategory3s;
    }
}
