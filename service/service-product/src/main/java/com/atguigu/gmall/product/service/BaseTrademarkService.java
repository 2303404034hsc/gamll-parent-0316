package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 19:22
 */
public interface BaseTrademarkService {
    List<BaseTrademark> getTrademarkList();

    IPage<BaseTrademark> getTrademarkListPage(Page<BaseTrademark> pageParam);

    BaseTrademark getById(String trademarkId);

    void save(BaseTrademark trademark);

    void update(BaseTrademark trademark);

    void removeById(Long trademarkId);
}
