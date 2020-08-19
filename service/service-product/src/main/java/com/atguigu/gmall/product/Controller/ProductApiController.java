package com.atguigu.gmall.product.Controller;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ccc
 * @create 2020-08-18 20:30
 */
@RestController
@RequestMapping("admin/product")
public class ProductApiController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @RequestMapping("testApiController")
    public String testApiController() {
        return "testApiController";
    }

    //http://api.gmall.com/admin/product/getCategory1
    @RequestMapping("getCategory1")
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Service.getCategory1();
        return baseCategory1List;
    }
}
