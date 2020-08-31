package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ccc
 * @create 2020-08-31 8:34
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    ListService listService;

    /**
     * 导入es的数据结构
     * @return
     */
    @GetMapping("inner/createIndex")
    public Result createIndex(){
        //调用es的api导入数据结构
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    //商品上架
    @RequestMapping("onSale/{skuId}")
    void onSale(@PathVariable("skuId") String skuId){
        listService.onSale(skuId);
    }

    //商品下架
    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") String skuId){
        listService.cancelSale(skuId);
    }

    //热点数据
    @RequestMapping("hotScore/{skuId}")
    void hotScore(@PathVariable("skuId") String skuId){
        listService.hotScore(skuId);
    }


}
