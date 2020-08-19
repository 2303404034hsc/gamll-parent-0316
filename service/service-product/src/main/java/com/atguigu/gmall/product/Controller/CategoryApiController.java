package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    BaseCategoryService baseCategory1Service;

    //一级分类列表
    @RequestMapping("getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Service.getCategory1();
        return Result.ok(baseCategory1List);
    }
}
