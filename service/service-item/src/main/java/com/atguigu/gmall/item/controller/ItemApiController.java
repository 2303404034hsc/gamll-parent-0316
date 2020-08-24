package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ccc
 * @create 2020-08-24 14:21
 */
@RestController
public class ItemApiController {
    @RequestMapping("api/item/{skuId}")
    Result getItem(@PathVariable("skuId") String skuId){
        return Result.ok(null);
    }
}
