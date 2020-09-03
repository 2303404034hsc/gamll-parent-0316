package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ccc
 * @create 2020-09-03 10:45
 */
@RestController
@RequestMapping("api/user")
public class UserApiController {

    @RequestMapping("inner/verify/{token}")
    public UserInfo verify(@PathVariable("token") String token){
        System.out.println("能过来");
        return null;
    }
}
