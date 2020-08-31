package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public IPage<BaseTrademark> getTrademarkListPage(Page<BaseTrademark> pageParam) {

        return trademarkMapper.selectPage(pageParam,null);
    }

    @Override
    public BaseTrademark getById(String trademarkId) {
        return trademarkMapper.selectById(trademarkId);
    }

    @Override
    public void save(BaseTrademark trademark) {
        trademarkMapper.insert(trademark);

    }

    @Override
    public void update(BaseTrademark trademark) {
        trademarkMapper.updateById(trademark);
    }

    @Override
    public void removeById(Long trademarkId) {
        trademarkMapper.deleteById(trademarkId);
    }

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        return trademarkMapper.selectById(tmId);
    }
}
