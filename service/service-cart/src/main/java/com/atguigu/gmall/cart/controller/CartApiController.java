package com.atguigu.gmall.cart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ccc
 * @create 2020-09-04 21:21
 */
@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @RequestMapping("cartTest")
    public String cartTest(HttpServletRequest request){

        String userId = request.getHeader("userId");

        return userId;
    }

}
