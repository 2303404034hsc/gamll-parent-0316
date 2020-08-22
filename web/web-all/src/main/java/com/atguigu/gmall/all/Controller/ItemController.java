package com.atguigu.gmall.all.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-08-22 10:08
 */
@Controller
public class ItemController {

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") String skuId){
        return "item/index";
    }
}
