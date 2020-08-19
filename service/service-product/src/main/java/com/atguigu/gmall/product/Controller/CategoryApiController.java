package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-19 10:01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class CategoryApiController {

    @Autowired
    BaseCategoryService baseCategoryService;

    //一级分类列表
    @RequestMapping("getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategoryService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    //二级分类列表
    @RequestMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") String category1Id) {

        List<BaseCategory2> baseCategory2List = baseCategoryService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }
    //三级分类列表
    @RequestMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") String category2Id) {

        List<BaseCategory3> baseCategory3List = baseCategoryService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

}
