package com.atguigu.gmall.list.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author ccc
 * @create 2020-08-31 11:15
 */
public interface GoodsElasticsearchRepository extends ElasticsearchRepository<Goods,Long> {
}
