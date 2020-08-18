package com.atguigu.gmall.product.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ccc
 * @create 2020-08-18 20:30
 */
@RestController
@RequestMapping("admin/product")
public class ProductApiController {

    @RequestMapping("testApiController")
    public String testApiController(){
        return "testApiController";
    }
}
