package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    //商品上架(es
    @RequestMapping("onSale/{skuId}")
    void onSale(@PathVariable("skuId") String skuId){
        listService.onSale(skuId);
    }

    //商品下架(es
    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") String skuId){
        listService.cancelSale(skuId);
    }

    //热点数据
    @RequestMapping("hotScore/{skuId}")
    void hotScore(@PathVariable("skuId") String skuId){
        listService.hotScore(skuId);
    }

    //查询list
    @PostMapping("list")
    Result list(@RequestBody SearchParam searchParam){

        SearchResponseVo searchResponseVo =  listService.list(searchParam);
        return Result.ok(searchResponseVo);
    }

}
