package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 18:48
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuApiController {

    @Autowired
    SpuInfoService spuInfoService;


    //http://api.gmall.com/admin/product/ {page}/{limit}?category3Id=61
    //spu分页列表
    @RequestMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable("page") Long page, @PathVariable("limit") Long limit, String category3Id) {

        Page<SpuInfo> pageParam = new Page<>(page,limit);

        IPage<SpuInfo> spuInfoIPage = spuInfoService.spuList(pageParam,category3Id);

        return Result.ok(spuInfoIPage);
    }
}
