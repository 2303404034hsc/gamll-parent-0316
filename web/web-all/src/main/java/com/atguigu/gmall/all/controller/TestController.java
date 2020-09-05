package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-08-22 9:40
 */
@Controller
public class TestController {

    @RequestMapping("test")
    public String test(){
        return "test";
    }
}
