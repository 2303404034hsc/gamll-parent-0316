package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 19:22
 */
@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {

    @Autowired
    BaseTrademarkMapper trademarkMapper;

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return trademarkMapper.selectList(null);
    }
}
